using System;
using System.Windows;
using System.Windows.Controls;

namespace Centrum_gier
{
    public partial class GraKolko : UserControl
    {
        private bool ruchX = true;
        private int liczbaRuchow = 0;

        public GraKolko()
        {
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            Button btn = (Button)sender;
            if (!string.IsNullOrEmpty(btn.Content as string)) return;

            if (ruchX) btn.Content = "X";
            else btn.Content = "O";

            liczbaRuchow++;
            if (SprawdzWygrana())
            {
                TxtStatus.Text = "Gracz " + (ruchX ? "X" : "O") + " wygrywa!";
                BlokujSiatke();
                return;
            }

            if (liczbaRuchow == 9)
            {
                TxtStatus.Text = "Remis!";
                return;
            }

            ruchX = !ruchX;
            TxtStatus.Text = "Ruch gracza: " + (ruchX ? "X" : "O");
        }

        private bool SprawdzWygrana()
        {
            if (Porownaj(B00, B01, B02) || Porownaj(B10, B11, B12) || Porownaj(B20, B21, B22)) return true;
            if (Porownaj(B00, B10, B20) || Porownaj(B01, B11, B21) || Porownaj(B02, B12, B22)) return true;
            if (Porownaj(B00, B11, B22) || Porownaj(B02, B11, B20)) return true;
            return false;
        }

        private bool Porownaj(Button b1, Button b2, Button b3)
        {
            string s1 = b1.Content as string;
            string s2 = b2.Content as string;
            string s3 = b3.Content as string;
            if (string.IsNullOrEmpty(s1)) return false;
            return s1 == s2 && s2 == s3;
        }

        private void BlokujSiatke()
        {
            B00.IsEnabled = B01.IsEnabled = B02.IsEnabled = false;
            B10.IsEnabled = B11.IsEnabled = B12.IsEnabled = false;
            B20.IsEnabled = B21.IsEnabled = B22.IsEnabled = false;
        }

        private void BtnReset_Click(object sender, RoutedEventArgs e)
        {
            ruchX = true;
            liczbaRuchow = 0;
            TxtStatus.Text = "Ruch gracza: X";
            Button[] tab = { B00, B01, B02, B10, B11, B12, B20, B21, B22 };
            foreach (var b in tab)
            {
                b.Content = string.Empty;
                b.IsEnabled = true;
            }
        }
    }
}
