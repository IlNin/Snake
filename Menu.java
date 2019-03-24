import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public class Menu extends JFrame {
 JPanel pannello; // Pannello principale a cui verranno aggiunti tutti gli altri componenti
 JPanel logo; // Panello dove verra' mostrato il logo del gioco
 JPanel pulsanti; // Pannello dove saranno posizionati i pulsanti
 JLabel[][] griglia; // Griglia di gioco
 char input1 = 'a'; // Input simulato che indica la direzione del primo serpente
 char input2 = 'd'; // Input simulato che indica la direzione del secondo serpente
 Snake snake1; // Primo serpente del logo
 Snake snake2; // Secondo serpente del logo
 ArrayList<Coordinate> lista; // Lista con le coordinate dove devono spawnare i frutti
 java.util.Timer timer; // Timer che regola il movimento dei serpenti
 java.util.Timer timer2; // Timer che regola lo spawning dei frutti
 int punteggioRecord; // Punteggio record del giocatore
 int punteggioRecordIA; // Punteggio record dell'IA 
  
 public Menu(int punteggioRecord, int punteggioRecordIA) {
  this.punteggioRecord = punteggioRecord; // Inizializza il record del giocatore
  this.punteggioRecordIA = punteggioRecordIA; // Inizializza il record dell'IA
  this.setTitle("SNAKE - Menu Principale"); // Titolo che appare in alto alla finestra
  setFocusable(true);
  requestFocus();
  
  pannello = new JPanel(); // Crea il pannello principale a cui gli altri due pannelli si agganceranno
  pannello.setVisible(true);
  pannello.setLayout(new BoxLayout(pannello, BoxLayout.PAGE_AXIS)); // Il pannello aggiungerà gli altri componenti a colonna
  this.setContentPane(pannello); // 'pannello' diventa il pannello principale
  
  logo = new JPanel(); // Crea il pannello che conterra' la griglia
  logo.setBackground(Color.BLACK); // Lo sfondo e' nero per distinguerlo dalle caselle bianche
  logo.setVisible(true);
  pannello.add(logo);
  creaGriglia();
  creaLista();
  
  pulsanti = new JPanel(); // Crea il pannello dove vengono inseriti i pulsanti
  pulsanti.setVisible(true);
  pulsanti.setLayout(new BoxLayout(pulsanti, BoxLayout.LINE_AXIS)); // Il pannello aggiungera' i componenti in sequenza su una riga
  pannello.add(pulsanti);
  
  JButton classica = new JButton("Classica"); // Crea il pulsante per iniziare una partita a giocatore singolo
  pulsanti.add(classica);
  JButton vs = new JButton("VS"); // Crea il pulsante per sfidare un giocatore o l'IA
  pulsanti.add(vs);
  JButton testIA = new JButton("Test IA"); // Crea il pulsante per testare l'IA
  pulsanti.add(testIA);
  JButton about = new JButton("About"); // Crea il pulsanti per le informazioni
  pulsanti.add(about);
  JButton esci = new JButton("Esci"); // Crea il pulsante per uscire
  pulsanti.add(esci);
  
  classica.addActionListener(new ActionListener() { // Permette al pulsante "Classica" di iniziare una nuova partita
   public void actionPerformed(ActionEvent e) {
    String[] opzioni = {"Lento", "Normale", "Veloce"}; // Sceglie la velocita' del serpente
	int scelta = JOptionPane.showOptionDialog(null, new JLabel("Scegli la velocita' del serpente!", JLabel.CENTER), "SNAKE - Classica", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, opzioni, opzioni[1]);
	if (scelta == JOptionPane.CLOSED_OPTION) {} // Il giocatore preme 'x' e ritorna al menu
	else {
	SnakeFrame nuovaPartita = new SnakeFrame(punteggioRecord, punteggioRecordIA, scelta+1); // Il giocatore sceglie correttamente una velocita'
	nuovaPartita.setSize(435, 296);
	nuovaPartita.setResizable(false);
	nuovaPartita.setVisible(true);
	nuovaPartita.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(false);
	timer.cancel();
	timer2.cancel();
    dispose(); } } });

  testIA.addActionListener(new ActionListener() { // Permette al pulsante "Test IA" di testare l'IA in una nuova partita
   public void actionPerformed(ActionEvent e) {
	String[] opzioni = {"Normale", "Benchmark"}; // Sceglie il tipo di test
	IASnake nuovaPartita;
	int scelta = JOptionPane.showOptionDialog(null, new JLabel("Scegli la modalita'!", JLabel.CENTER), "SNAKE - Test IA", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, opzioni, opzioni[0]);
	if (scelta == JOptionPane.CLOSED_OPTION) {}
	else {
	 if (scelta == 0) {
	  nuovaPartita = new IASnake(punteggioRecordIA, punteggioRecord, 0, 0, false); }
	 else {
	  JOptionPane.showMessageDialog(null, new JLabel("Premi 'e' per interrompere il benchmark!", JLabel.CENTER), "SNAKE - Test IA Benchmark", JOptionPane.PLAIN_MESSAGE);
	  nuovaPartita = new IASnake(punteggioRecordIA, punteggioRecord, 1, 0, true); }
	 nuovaPartita.setSize(435, 296);
	 nuovaPartita.setResizable(false);
	 nuovaPartita.setVisible(true);
	 nuovaPartita.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     setVisible(false);
	 timer.cancel();
     timer2.cancel();
     dispose();  } } });
  
  vs.addActionListener(new ActionListener() { // Permette al pulsante "VS" di iniziare una partita per due giocatori
   public void actionPerformed(ActionEvent e) {
    String[] opzioni = {"Giocatore 2", "Computer"}; // Sceglie se sfidare un altra persona o la CPU
	Vs nuovaPartita;
	int scelta = JOptionPane.showOptionDialog(null, new JLabel("Scegli contro chi giocare!", JLabel.CENTER), "SNAKE - VS", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, opzioni, opzioni[0]);
    if (scelta == JOptionPane.CLOSED_OPTION) {}
	else {
	 if (scelta == 0) {
	  nuovaPartita = new Vs(punteggioRecord, punteggioRecordIA, true); }
	 else {
	  nuovaPartita = new Vs(punteggioRecord, punteggioRecordIA, false); }
    nuovaPartita.setSize(435, 535);
    nuovaPartita.setResizable(false);
    nuovaPartita.setVisible(true);
    nuovaPartita.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(false);
	timer.cancel();
	timer2.cancel();
    dispose(); } } });

  about.addActionListener(new ActionListener() { // Permette al pulsante "About" di mostrare la spiegazione
   public void actionPerformed(ActionEvent e) {
    JOptionPane.showMessageDialog(null, "- BENVENUTO A SNAKE! -\n\n- MODALITA' -\nClassica: Controlla il serpente e mangia i frutti rossi senza toccare il bordo o la tua stessa coda!\nVS: Affronta un tuo amico o la CPU in una sfida fino all'ultimo frutto!\nTest IA: Osserva un prototipo di CPU mentra cerca di giocare a Snake!\n\n- COMANDI -\n'awsd': Muovi il serpente nella griglia!\nFrecce direzionali: Lo stesso, ma per il secondo giocatore!\n'p': Metti in pausa il gioco!\n'e': Se il gioco e' in pausa, torna al menu!\n\n>>> Creato da Luca Sannino, Maggio-Giugno 2016 <<<", "SNAKE - About", JOptionPane.PLAIN_MESSAGE); } });
  
  esci.addActionListener(new ActionListener() { // Permette al pulsante "Esci" di uscire
   public void actionPerformed(ActionEvent e) {
    timer.cancel();
	timer2.cancel();
	setVisible(false);
	dispose(); } });
   
  snake1 = new Snake(0); // Inizializza i serpenti e timers per l'animazione del logo
  snake2 = new Snake(1);
  timer = new java.util.Timer();
  timer.schedule(new animazione(), 0, 100);
  timer2 = new java.util.Timer();
  timer2.schedule(new inserisciFrutto(), 0, 500); }
  
 public void creaGriglia() { // Prepara la griglia sulla quale avviene l'animazione di apertura
  griglia = new JLabel[11][25];
  for (int i = 0; i < 11; i++) {
   for (int j = 0; j < 25; j++) {
    griglia[i][j] = new JLabel("     "); // Le caselle sono costituite da delle JLabel vuote colorate di bianco
	griglia[i][j].setBackground(Color.WHITE);
	griglia[i][j].setOpaque(true);
    logo.add(griglia[i][j]); } }
	
  // Prepara la lettera "s"
  griglia[7][3].setBackground(Color.BLUE); 
  griglia[7][4].setBackground(Color.BLUE);
  griglia[6][5].setBackground(Color.BLUE);
  griglia[5][5].setBackground(Color.BLUE);
  griglia[5][4].setBackground(Color.BLUE);
  griglia[5][3].setBackground(Color.BLUE);
  griglia[4][3].setBackground(Color.BLUE);
  griglia[3][4].setBackground(Color.BLUE);
  griglia[3][5].setBackground(Color.BLUE);
  
  // Prepara la lettera "n"
  griglia[7][7].setBackground(Color.BLUE);
  griglia[6][7].setBackground(Color.BLUE);
  griglia[5][7].setBackground(Color.BLUE);
  griglia[4][8].setBackground(Color.BLUE);
  griglia[3][8].setBackground(Color.BLUE);
  griglia[7][9].setBackground(Color.BLUE);
  griglia[6][9].setBackground(Color.BLUE);
  griglia[5][9].setBackground(Color.BLUE);
  
  // Prepara la lettera "a"
  griglia[7][11].setBackground(Color.BLUE);
  griglia[7][12].setBackground(Color.BLUE);
  griglia[6][11].setBackground(Color.BLUE);
  griglia[5][11].setBackground(Color.BLUE);
  griglia[3][11].setBackground(Color.BLUE);
  griglia[3][12].setBackground(Color.BLUE);
  griglia[4][13].setBackground(Color.BLUE);
  griglia[5][13].setBackground(Color.BLUE);
  griglia[5][12].setBackground(Color.BLUE);
  griglia[6][13].setBackground(Color.BLUE);
  griglia[7][13].setBackground(Color.BLUE);
  
  // Prepara la lettera "k"
  griglia[7][15].setBackground(Color.BLUE);
  griglia[6][15].setBackground(Color.BLUE);
  griglia[5][15].setBackground(Color.BLUE);
  griglia[4][15].setBackground(Color.BLUE);
  griglia[3][15].setBackground(Color.BLUE);
  griglia[6][16].setBackground(Color.BLUE);
  griglia[4][17].setBackground(Color.BLUE);
  griglia[5][17].setBackground(Color.BLUE);
  griglia[7][17].setBackground(Color.BLUE);
  
  // Prepara la lettera "e"
  griglia[6][19].setBackground(Color.BLUE);
  griglia[5][19].setBackground(Color.BLUE);
  griglia[4][19].setBackground(Color.BLUE);
  griglia[3][20].setBackground(Color.BLUE);
  griglia[4][21].setBackground(Color.BLUE);
  griglia[5][21].setBackground(Color.BLUE);
  griglia[5][20].setBackground(Color.BLUE);
  griglia[7][20].setBackground(Color.BLUE);
  griglia[7][21].setBackground(Color.BLUE);
  
  // Crea il primo serpente
  griglia[9][11].setBackground(Color.BLUE);
  griglia[9][12].setBackground(Color.BLUE);
  griglia[9][13].setBackground(Color.BLUE);
  griglia[9][14].setBackground(Color.BLUE);

  // Crea il secondo serpente
  griglia[1][15].setBackground(Color.BLUE);
  griglia[1][14].setBackground(Color.BLUE);
  griglia[1][13].setBackground(Color.BLUE);
  griglia[1][12].setBackground(Color.BLUE);  }
 
 class animazione extends TimerTask { // Muove i serpenti nella direzione del loro input
  public void run() {
   muoviSnake(snake1, 1, input1);
   muoviSnake(snake2, 2, input2);  } }
 
 public void creaLista() { // Crea una lista con tutte le coordinate possibile dove inserire un frutto
  lista = new ArrayList<Coordinate>();
  for (int i = 1; i < 10; i++) {
   lista.add(new Coordinate(i, 1));
   lista.add(new Coordinate(i, 23)); }
  for (int i = 1; i < 24; i++) {
   lista.add(new Coordinate(1, i));
   lista.add(new Coordinate(9, i)); } }
 
 class inserisciFrutto extends TimerTask { // Posiziona casualmente un frutto in un punto indicato dalla lista di coordinate
  public void run() {
   boolean flag = false;
   while (flag == false) {
    Random rand = new Random();
    int scelta = rand.nextInt(lista.size());
    if (griglia[lista.get(scelta).getX()][lista.get(scelta).getY()].getBackground() == Color.WHITE) {
     griglia[lista.get(scelta).getX()][lista.get(scelta).getY()].setBackground(Color.RED);
     flag = true; } } } }
 
 public void muoviSnake(Snake snake, int i, char input) { // Aggiorna la posizione del serpente 
  int aggiungiX = 0;
  int aggiungiY = 0;
  if (input == 'w') { // Calcola di quanto si deve muovere la testa a seconda dell'input scelto
   aggiungiX = -1; }
  else if (input == 's') {
   aggiungiX = 1; }
  else if (input == 'd') {
   aggiungiY = 1; }
  else if (input == 'a') {
   aggiungiY = -1; }
  
  Iterator it = snake.getElementi().iterator(); // Iteratore che scorre tutti gli elementi del serpente
  boolean testa = true; // Controlla se attualmente si sta muovendo la testa del serpente
  int ultimoX = 0; // Ultimo X ad essere stato mosso
  int ultimoY = 0; // Ultimo Y ad essere stato mosso
  int swapX = 0;
  int swapY = 0;
  
  while (it.hasNext()) { // Scorre gli elementi del serpente
   Coordinate correnti = (Coordinate) it.next();
   if (testa == true) { // Stiamo muovendo l'elemento di testa
    ultimoX = correnti.getX(); // Salva le coordinate attuali della testa
	ultimoY = correnti.getY(); 
    correnti.setX(ultimoX+aggiungiX); // La testa si sposta e assume nuove coordinate
    correnti.setY(ultimoY+aggiungiY);
    griglia[correnti.getX()][correnti.getY()].setBackground(Color.BLUE);
	if (correnti.getX() == 9 && correnti.getY() == 1) { // Aggiorna l'input, e quindi la direzione, in base alla posizione del serpente
	 if (i == 1) {
	  input1 = 'w'; }
	 else {
	  input2 = 'w'; } }
	if (correnti.getX() == 1 && correnti.getY() == 1) {
	 if (i == 1) {
	  input1 = 'd'; }
	 else {
	  input2 = 'd'; } }
	if (correnti.getX() == 1 && correnti.getY() == 23) {
	 if (i == 1) {
	  input1 = 's'; }
	 else {
	  input2 = 's'; } }
	if (correnti.getX() == 9 && correnti.getY() == 23) {
	 if (i == 1) {
	  input1 = 'a'; }
	 else {
	  input2 = 'a'; } }
    testa = false;	}
   else { // Stiamo muovendo un elemento interno al serpente, che imita il movimento di quello successivo
    swapX = correnti.getX();
	swapY = correnti.getY();
	correnti.setX(ultimoX);
	correnti.setY(ultimoY);
	griglia[ultimoX][ultimoY].setBackground(Color.BLUE);
	ultimoX = swapX;
	ultimoY = swapY; } }
  griglia[ultimoX][ultimoY].setBackground(Color.WHITE);	} }
  