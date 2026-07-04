using System;
using System.Globalization;
using System.Windows;
using System.Windows.Data;
using System.Windows.Forms;

namespace DesktopClient
{
    public class CanvasBorderHeightConverter : IMultiValueConverter
    {
        public object Convert(object[] values, Type targetType, object parameter, CultureInfo culture)
        {
            double borderW = (double)values[0];
            double offsetBcusBorder = ((Thickness)values[1]).Left;

            double ratio = (double)Screen.PrimaryScreen.Bounds.Height / Screen.PrimaryScreen.Bounds.Width;
            if (ratio < 1)
            {
                double canvasW = borderW - offsetBcusBorder*2;
                double canvasH = canvasW * ratio;
                return canvasH + offsetBcusBorder;
            }
            return DependencyProperty.UnsetValue;
        }

        public object[] ConvertBack(object value, Type[] targetTypes, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}
