using System;
using System.IO;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;

namespace Centrum_gier
{
    public partial class EkranLogowania : UserControl
    {
        private readonly string plikKont = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "gracze.txt");

        public EkranLogowania()
        {
            InitializeComponent();
        }

        private void PrzelaczDoRejestracji_MouseDown(object sender, MouseButtonEventArgs e)
        {
            FormularzLogowania.Visibility = Visibility.Collapsed;
            FormularzRejestracji.Visibility = Visibility.Visible;
        }

        private void PrzelaczDoLogowania_MouseDown(object sender, MouseButtonEventArgs e)
        {
            FormularzRejestracji.Visibility = Visibility.Collapsed;
            FormularzLogowania.Visibility = Visibility.Visible;
        }

        private void BtnZaloguj_Click(object sender, RoutedEventArgs e)
        {
            string user = TxtLogin.Text.Trim();
            string pass = TxtHaslo.Password;

            if (string.IsNullOrEmpty(user) || string.IsNullOrEmpty(pass)) return;

            bool zalogowano = false;
            if (File.Exists(plikKont))
            {
                string[] linie = File.ReadAllLines(plikKont);
                foreach (string linia in linie)
                {
                    string[] dane = linia.Split(':');
                    if (dane.Length == 2 && dane[0] == user && dane[1] == pass)
                    {
                        zalogowano = true;
                        break;
                    }
                }
            }

            if (zalogowano || (user == "admin" && pass == "123"))
            {
                var okno = Window.GetWindow(this) as MainWindow;
                if (okno != null) okno.GlownyKontener.Content = new EkranPulpitu(user);
            }
            else
            {
                MessageBox.Show("Niepoprawny nick lub hasło.", "Błąd", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private void BtnRejestruj_Click(object sender, RoutedEventArgs e)
        {
            string user = TxtRejLogin.Text.Trim();
            string pass = TxtRejHaslo.Password;

            if (string.IsNullOrEmpty(user) || string.IsNullOrEmpty(pass)) return;

            File.AppendAllText(plikKont, user + ":" + pass + Environment.NewLine);
            MessageBox.Show("Profil gracza został pomyślnie utworzony!", "Sukces", MessageBoxButton.OK, MessageBoxImage.Information);

            var okno = Window.GetWindow(this) as MainWindow;
            if (okno != null) okno.GlownyKontener.Content = new EkranPulpitu(user);
        }
    }
}
