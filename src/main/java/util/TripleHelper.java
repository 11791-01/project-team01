package util;

public class TripleHelper {

  String s;
  String p;
  String o;
  
  public TripleHelper(String s, String p, String o) {
    this.s = s;
    this.p = p;
    this.o = o;
  }
  
  @Override
  public boolean equals(Object o) {
    TripleHelper other = (TripleHelper) o;
    if (this.s.equals(other.s) && this.p.equals(other.p) && this.o.equals(other.o))
      return true;
    else
      return false;
  }
}
