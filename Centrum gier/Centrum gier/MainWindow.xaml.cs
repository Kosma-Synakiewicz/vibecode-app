using System;
using System.Windows;
using System.Windows.Input;

namespace Centrum_gier
{
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
            GlownyKontener.Content = new EkranLogowania();
        }

        private void PasekGorny_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (e.ChangedButton == MouseButton.Left)
            {
                this.DragMove();
            }
        }

        private void BtnMinimalizuj_Click(object sender, RoutedEventArgs e)
        {
            this.WindowState = WindowState.Minimized;
        }

        private void BtnZamknij_Click(object sender, RoutedEventArgs e)
        {
            this.Close();
        }
    }
}
