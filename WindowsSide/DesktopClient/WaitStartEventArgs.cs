using System;

namespace DesktopClient
{
    public class WaitStartEventArgs  : EventArgs
    {
        public bool ForReconnect { get; private set; }
        public WaitStartEventArgs(bool forReconnect)
        {
            this.ForReconnect = forReconnect;
        }
    }
}
