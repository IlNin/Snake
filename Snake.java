import java.util.*;

public class Snake {
 private ArrayList<Coordinate> elementi; // Lista che contiene le coordinate degli elementi che costituiscono il serpente
 
 public Snake() { // Costrutture di default: crea il serpente per il gioco
  elementi = new ArrayList<Coordinate>();
  elementi.add(new Coordinate(10, 6));
  elementi.add(new Coordinate(10, 5));
  elementi.add(new Coordinate(10, 4));
  elementi.add(new Coordinate(10, 3));
  elementi.add(new Coordinate(10, 2));
  elementi.add(new Coordinate(10, 1));
  elementi.add(new Coordinate(10, 0)); }
 
 public Snake(int i) { // Costrutture alternativo: crea i due serpenti per il menu
  if (i == 0) {
   elementi = new ArrayList<Coordinate>();
   elementi.add(new Coordinate(9, 11));
   elementi.add(new Coordinate(9, 12));
   elementi.add(new Coordinate(9, 13));
   elementi.add(new Coordinate(9, 14)); }
  else if (i == 1) {
   elementi = new ArrayList<Coordinate>();
   elementi.add(new Coordinate(1, 15));
   elementi.add(new Coordinate(1, 14));
   elementi.add(new Coordinate(1, 13));
   elementi.add(new Coordinate(1, 12)); } }
 
 public ArrayList<Coordinate> getElementi() {
  return elementi; }
}