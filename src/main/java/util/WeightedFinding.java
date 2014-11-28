package util;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Finding;

public class WeightedFinding {
  
  Finding fndg;
  double newsco;
  
  public WeightedFinding(Finding fnd, double sco) {
    this.fndg = fnd;
    this.newsco = sco;
  }
  public Finding getfndg(){
    return fndg;
  }
  public Double getnewSco(){
    return newsco;
  }
  public void setFndg(Finding fnd){
    this.fndg=fnd;
  }
  public void setNewSco(Double sco){
    this.newsco=sco;
  }
}
