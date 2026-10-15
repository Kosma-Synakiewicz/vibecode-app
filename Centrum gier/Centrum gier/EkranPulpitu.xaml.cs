using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;

namespace Centrum_gier
{
    public partial class EkranPulpitu : UserControl
    {
        public EkranPulpitu(string gracz)
        {
            InitializeComponent();
            TxtGraczInfo.Text = "Gracz: " + gracz;
            KontenerGier.Content = new GraKolko();
        }

        private void BtnGrajKolko_Click(object sender, RoutedEventArgs e)
        {
            BtnGrajKolko.Background = new SolidColorBrush((Color)ColorConverter.ConvertFromString("#2B303C"));
            BtnGrajKolko.Foreground = Brushes.White;
            BtnGrajSnake.Background = Brushes.Transparent;
            BtnGrajSnake.Foreground = new SolidColorBrush((Color)ColorConverter.ConvertFromString("#A2A6AC"));

            KontenerGier.Content = new GraKolko();
        }

        private void BtnGrajSnake_Click(object sender, RoutedEventArgs e)
        {
            BtnGrajSnake.Background = new SolidColorBrush((Color)ColorConverter.ConvertFromString("#2B303C"));
            BtnGrajSnake.Foreground = Brushes.White;
            BtnGrajKolko.Background = Brushes.Transparent;
            BtnGrajKolko.Foreground = new SolidColorBrush((Color)ColorConverter.ConvertFromString("#A2A6AC"));

            KontenerGier.Content = new GraSnake();
        }

        private void BtnWyloguj_Click(object sender, RoutedEventArgs e)
        {
            var okno = Window.GetWindow(this) as MainWindow;
            if (okno != null) okno.GlownyKontener.Content = new EkranLogowania();
        }
    }
}
