using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Shapes;
using System.Windows.Threading;

namespace Centrum_gier
{
    public class KlasaKomendy : ICommand
    {
        private readonly Action<object?> _execute;
        public KlasaKomendy(Action<object?> execute) => _execute = execute;
        public bool CanExecute(object? parameter) => true;
        public void Execute(object? parameter) => _execute(parameter);
        public event EventHandler? CanExecuteChanged;
    }

    public partial class GraSnake : UserControl
    {
        private enum Kierunek { Lewo, Prawo, Gora, Dol }
        private Kierunek aktualnyKierunek = Kierunek.Prawo;
        private DispatcherTimer timer;
        private List<Point> segmentyWeza = new List<Point>();
        private Point pozycjaJedzenia;
        private int wynik = 0;
        private const int RozmiarKlocka = 15;

        public ICommand KierunekGoraCommand { get; }
        public ICommand KierunekDolCommand { get; }
        public ICommand KierunekLewoCommand { get; }
        public ICommand KierunekPrawoCommand { get; }

        public GraSnake()
        {
            InitializeComponent();
            this.DataContext = this;

            KierunekGoraCommand = new KlasaKomendy(p => { if (aktualnyKierunek != Kierunek.Dol) aktualnyKierunek = Kierunek.Gora; });
            KierunekDolCommand = new KlasaKomendy(p => { if (aktualnyKierunek != Kierunek.Gora) aktualnyKierunek = Kierunek.Dol; });
            KierunekLewoCommand = new KlasaKomendy(p => { if (aktualnyKierunek != Kierunek.Prawo) aktualnyKierunek = Kierunek.Lewo; });
            KierunekPrawoCommand = new KlasaKomendy(p => { if (aktualnyKierunek != Kierunek.Lewo) aktualnyKierunek = Kierunek.Prawo; });

            timer = new DispatcherTimer();
            timer.Interval = TimeSpan.FromMilliseconds(120);
            timer.Tick += PętlaGry_Tick;
        }

        private void UserControl_Unloaded(object sender, RoutedEventArgs e)
        {
            timer.Stop();
        }

        private void BtnStart_Click(object sender, RoutedEventArgs e)
        {
            timer.Stop();
            wynik = 0;
            TxtWynik.Text = "Wynik: " + wynik;
            aktualnyKierunek = Kierunek.Prawo;

            segmentyWeza.Clear();
            segmentyWeza.Add(new Point(60, 60));
            segmentyWeza.Add(new Point(45, 60));
            segmentyWeza.Add(new Point(30, 60));

            GenerujJedzenie();
            RysujObiekty();
            timer.Start();
        }

        private void PętlaGry_Tick(object sender, EventArgs e)
        {
            Point glowa = segmentyWeza[0];
            Point nowaGlowa = glowa;

            switch (aktualnyKierunek)
            {
                case Kierunek.Lewo: nowaGlowa.X -= RozmiarKlocka; break;
                case Kierunek.Prawo: nowaGlowa.X += RozmiarKlocka; break;
                case Kierunek.Gora: nowaGlowa.Y -= RozmiarKlocka; break;
                case Kierunek.Dol: nowaGlowa.Y += RozmiarKlocka; break;
            }

            if (nowaGlowa.X < 0 || nowaGlowa.X >= 300 || nowaGlowa.Y < 0 || nowaGlowa.Y >= 300 || segmentyWeza.Contains(nowaGlowa))
            {
                timer.Stop();
                MessageBox.Show("Koniec gry! Twój wynik to: " + wynik, "Game Over", MessageBoxButton.OK, MessageBoxImage.Information);
                return;
            }

            segmentyWeza.Insert(0, nowaGlowa);

            if (nowaGlowa == pozycjaJedzenia)
            {
                wynik += 10;
                TxtWynik.Text = "Wynik: " + wynik;
                GenerujJedzenie();
            }
            else
            {
                segmentyWeza.RemoveAt(segmentyWeza.Count - 1);
            }

            RysujObiekty();
        }

        private void GenerujJedzenie()
        {
            Random rnd = new Random();
            int maxKlockow = 300 / RozmiarKlocka;

            do
            {
                int x = rnd.Next(0, maxKlockow) * RozmiarKlocka;
                int y = rnd.Next(0, maxKlockow) * RozmiarKlocka;
                pozycjaJedzenia = new Point(x, y);
            }
            while (segmentyWeza.Contains(pozycjaJedzenia));
        }

        private void RysujObiekty()
        {
            ObszarGry.Children.Clear();

            Ellipse jedzenie = new Ellipse { Width = RozmiarKlocka, Height = RozmiarKlocka, Fill = Brushes.Red };
            Canvas.SetLeft(jedzenie, pozycjaJedzenia.X);
            Canvas.SetTop(jedzenie, pozycjaJedzenia.Y);
            ObszarGry.Children.Add(jedzenie);

            for (int i = 0; i < segmentyWeza.Count; i++)
            {
                Rectangle segment = new Rectangle { Width = RozmiarKlocka, Height = RozmiarKlocka, Fill = (i == 0) ? Brushes.LimeGreen : Brushes.Green };
                Canvas.SetLeft(segment, segmentyWeza[i].X);
                Canvas.SetTop(segment, segmentyWeza[i].Y);
                ObszarGry.Children.Add(segment);
            }
        }
    }
}
