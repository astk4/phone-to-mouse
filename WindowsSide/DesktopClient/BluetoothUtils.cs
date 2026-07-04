using InTheHand.Net.Bluetooth;
using InTheHand.Net.Sockets;
using MouseSimTestEnvironment;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Threading;
using System.Windows.Forms;

namespace DesktopClient
{
    public class BluetoothUtils
    {
        private const int maxDevices = 255;
        private bool listenerStarted = false;
        public bool ListenerStarted { get { return listenerStarted; } }
        private bool connectedToPhone = false;

        private IList<BluetoothDeviceInfo> visibles = Array.Empty<BluetoothDeviceInfo>();
        private IList<BluetoothDeviceInfo> paired = Array.Empty<BluetoothDeviceInfo>();

        private readonly BluetoothClient _clientForDiscovery = new BluetoothClient();
        private BluetoothClient _clientOfSecondDevice = null!;

        private readonly BluetoothListener _listener;
        public AsyncCallback OnDiscoverVisibleCompleted, OnDiscoverPairedCompleted,
                             OnClientFound;
        public event EventHandler OnDisconnected;
        public event EventHandler<WaitStartEventArgs> OnStartedWaitingForConnection;

        private readonly CanvasDrawer _drawer;

        public BluetoothUtils(CanvasDrawer drawer)
        {
            this._drawer = drawer;

            string guidStr = File.ReadAllText("guid.txt");
            _listener = new BluetoothListener(Guid.Parse(guidStr));
            
            OnDiscoverPairedCompleted = new AsyncCallback((result) => paired = endDiscoveryReturnPhones(result));
            OnDiscoverVisibleCompleted = new AsyncCallback((result) => visibles = endDiscoveryReturnPhones(result));

            OnClientFound = new AsyncCallback((result) =>
            {
                _clientOfSecondDevice = _listener.EndAcceptBluetoothClient(result);
                connectedToPhone = true;

                SendScreenDimensions(_clientOfSecondDevice);

                Thread _listeningThread = new Thread(new ParameterizedThreadStart(ListenAndCatchData));

                _listeningThread.Start(_clientOfSecondDevice);
            });
        }

        private static void SendScreenDimensions(object stream)
        {
            int[] dimensions = new int[2] { Screen.PrimaryScreen.Bounds.Width, Screen.PrimaryScreen.Bounds.Height };
            byte[] arrBeingSent = new byte[dimensions.Length*sizeof(int)+1];
            arrBeingSent[0] = (byte)MouseCommands.Ratio;
            Buffer.BlockCopy(dimensions, 0, arrBeingSent, 1, arrBeingSent.Length-1);

            BluetoothClient client = (stream as BluetoothClient)!;
            client.GetStream().WriteAsync(arrBeingSent, 0, arrBeingSent.Length);
        }

        public void Disconnect(bool sendResponse, bool fireEvent)
        {
            if (sendResponse && _clientOfSecondDevice != null)
            {
                _clientOfSecondDevice.GetStream().WriteAsync(new byte[] { (byte)MouseCommands.Disconnect });
            }
            _listener.Stop();
            connectedToPhone = listenerStarted = false;

            if (fireEvent)
            {
                OnDisconnected.Invoke(this, EventArgs.Empty);
            }
        }

        private void ListenAndCatchData(object obj)
        {
            BluetoothClient cl = (obj as BluetoothClient)!;
            bool leftBtn = true;
            float xPercent = 0, yPercent = 0;
            while (connectedToPhone)
            {

                int firstByteVal = cl.GetStream().ReadByte();
                if(firstByteVal == -1) { continue; }

                byte[] receiveBuffer;
                switch((MouseCommands)firstByteVal)
                {
                    case MouseCommands.Wheel:
                        receiveBuffer = new byte[2];
                        break;
                    case MouseCommands.Disconnect:
                        receiveBuffer = new byte[3];
                        break;
                    default:
                        receiveBuffer = new byte[10];
                        break;
                    
                }
                receiveBuffer[0] = (byte)firstByteVal;
                cl.GetStream().Read(receiveBuffer, 1, receiveBuffer.Length - 1);

                singleEventProcessing(receiveBuffer, ref leftBtn, ref xPercent, ref yPercent);
            }
        }

#if DEBUG
        private static void debugShowArray(byte[] arr)
        {
            Debug.Write("[ ");
            foreach(byte b in arr)
            {
                Debug.Write($"{b} ");
            }
            Debug.WriteLine(']');
        }
#endif

        private void singleEventProcessing(byte[] buffer, ref bool leftBtn, ref float xPercent, ref float yPercent)
        {
            if (buffer[0] <= 4)
            {
                leftBtn = Convert.ToBoolean(buffer[1]);
                xPercent = BitConverter.ToSingle(buffer, 2);
                yPercent = BitConverter.ToSingle(buffer, 6);
            }
            int xWinapi = (int)(xPercent*InputSimWrapper.ScreenAnyAxisMax),
                yWinapi = (int)(yPercent*InputSimWrapper.ScreenAnyAxisMax);
            
            #if DEBUG
                debugShowArray(buffer);
            #endif

            switch ((MouseCommands)buffer[0])
            {
                case MouseCommands.Disconnect:
                    this.Disconnect(Convert.ToBoolean(buffer[1]), true);
                    if(Properties.Settings.Default.WaitForReconnect && !Convert.ToBoolean(buffer[2]))
                    {
                        StartWaitingForConnection(true);
                    }
                    break;
                case MouseCommands.Move:

                    byte buttonByte = buffer[1];

                    InputSimWrapper.mouse_move(xWinapi, yWinapi);

                    if(buttonByte > 1)
                    {
                        _drawer.DrawOnlyPoint(xPercent, yPercent, null);
                    }
                    else {
                        _drawer.DrawPolylineSegment(xPercent, yPercent);
                    }
                    break;
                case MouseCommands.Click:
                    { 
                        if(leftBtn) {
                            InputSimWrapper.left_click(xWinapi, yWinapi);
                        }
                        else {
                            InputSimWrapper.right_click(xWinapi, yWinapi);
                        }
                        _drawer.DrawOnlyPoint(xPercent, yPercent, leftBtn);
                    }
                    break;
                case MouseCommands.DoubleClick:
                    {
                        for(byte i = 0; i<2; i++)
                        {
                            if (leftBtn) {
                                InputSimWrapper.left_click(xWinapi, yWinapi);
                            }
                            else {
                                InputSimWrapper.right_click(xWinapi, yWinapi);
                            }
                        }
                        _drawer.DrawOnlyPoint(xPercent, yPercent, leftBtn);
                    }
                    break;
                case MouseCommands.Down:
                    if(leftBtn) {
                        InputSimWrapper.left_key_down(xWinapi, yWinapi);
                    }
                    else {
                        InputSimWrapper.right_key_down(xWinapi, yWinapi);
                    }  
                    _drawer.DrawOnlyPoint(xPercent, yPercent, leftBtn);
                    _drawer.BeginPolyline(xPercent, yPercent, leftBtn);
                    break;
                case MouseCommands.Up:
                    if (leftBtn) {
                        InputSimWrapper.left_key_up(xWinapi, yWinapi);
                    }
                    else {
                        InputSimWrapper.right_key_up(xWinapi, yWinapi);
                    }
                    _drawer.DrawOnlyPoint(xPercent, yPercent, null);
                    break;
                case MouseCommands.Wheel:
                    {
                        bool scrollUp = Convert.ToBoolean(buffer[1]);

                        if (scrollUp) {
                            InputSimWrapper.wheel_forward(1);
                        }
                        else {
                            InputSimWrapper.wheel_backward(1);
                        }
                        _drawer.WriteWheelScrollInfo(scrollUp);
                    }
                    break;
            }

            if (buffer[0]>0 && buffer[0] < 5)
            {
                _drawer.WriteButtonGestureInfo((MouseCommands)buffer[0], xPercent, yPercent, leftBtn);
            }
        }

        private static bool DeviceCanBePhone(BluetoothDeviceInfo bdi)
        {
            switch(bdi.ClassOfDevice.MajorDevice)
            {
                case DeviceClass.Phone:
                case DeviceClass.Uncategorized:
                case DeviceClass.Miscellaneous:
                    return true;
            }
            return false;
        }

        public IEnumerable<string> VisibleDevicesNames
        {
            get { return visibles.Select(x => x.DeviceName); }
        }
        public IEnumerable<string> PairedDevicesNames
        {
            get { return paired.Select(x => x.DeviceName); }
        }

        private IList<BluetoothDeviceInfo> endDiscoveryReturnPhones(IAsyncResult result)
        {
            return _clientForDiscovery.EndDiscoverDevices(result).Where(d => DeviceCanBePhone(d)).ToArray();
        }

        public void DiscoverAll()
        {
            _clientForDiscovery.BeginDiscoverDevices(maxDevices, false, false, true, true, OnDiscoverVisibleCompleted, null);
            _clientForDiscovery.BeginDiscoverDevices(maxDevices, true, false, false, false, OnDiscoverPairedCompleted, null);
        }
        public void DiscoverVisible()
        {
            _clientForDiscovery.BeginDiscoverDevices(maxDevices, false, false, true, true, OnDiscoverVisibleCompleted, null);
        }
        public void DiscoverPaired()
        {
            _clientForDiscovery.BeginDiscoverDevices(maxDevices, true, false, false, false, OnDiscoverPairedCompleted, null);
        }

        public void CancelDiscoveryOfAll() => _clientForDiscovery.EndDiscoverDevices(null);

        public void StartWaitingForConnection(bool waitForReconnect = false)
        {
            if (!listenerStarted)
            {
                _listener.Start();
                listenerStarted = true;
            }
            OnStartedWaitingForConnection?.Invoke(this, new WaitStartEventArgs(waitForReconnect));
            _listener.BeginAcceptBluetoothClient(OnClientFound, null);
        }
    }
}
