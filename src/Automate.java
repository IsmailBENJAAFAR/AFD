/*
 * fichier.txt:
 * *****************************************************
 * nbre d'alpahbet,nbre d'etats,nbre d'etats finaux    *
 * alphabet1,alphabet2....,alphabetn                   *
 * etat1,etat2.............,etatn                      *
 * etatfinal1,.....etatfinaln                          *
 * etat_initial                                        *
 * etat1,alphabet,etat_suivant                         *
 * eta2,alphabet,etat_suivant                          *
 *  .                                                  *
 *  .                                                  *
 *  .                                                  *
 *  .                                                  *
 * etatn,alphabet,etatsuivant                          *
 * *****************************************************
 * fin fichier
 */
import java.io.*;
import java.util.*;
public class Automate{
    String nomfich;//nom du fichier
    String[] x;//alphabet Sigma
    String[] q;//Etaq Q
    String qi;//etat initial q0
    String[] f;//etats finaux F
    ArrayList<String> calculationSteps = new ArrayList<String>();
    public Automate(String nomf)throws IOException {
        int nx,ne,nf;
        String lin;
        nomfich=nomf;
        BufferedReader fin =new BufferedReader(new FileReader(nomfich));
        lin=fin.readLine();
        StringTokenizer st=new StringTokenizer(lin," ?,.;:");
        nx=Integer.parseInt(st.nextToken());
        x=new String[nx];
        ne=Integer.parseInt(st.nextToken());
        q=new String[ne];
        nf=Integer.parseInt(st.nextToken());
        f=new String[nf];
        lin=fin.readLine();
        st=new StringTokenizer(lin," ?,.;:");
        int i=0;
        while(st.hasMoreTokens()) {
            x[i]=st.nextToken();
            i++;
        }
        lin=fin.readLine();
        st=new StringTokenizer(lin," ?,.;:");
        i=0;
        while(st.hasMoreTokens()) {
            q[i]=st.nextToken();
            i++;
        }

        lin=fin.readLine();
        st=new StringTokenizer(lin," ?,.;:");
        i=0;
        while(st.hasMoreTokens()) {
            f[i]=st.nextToken();
            i++;
        }
        lin=fin.readLine();
        st=new StringTokenizer(lin," ?,.;:");
        qi=st.nextToken();
    }

    // fonction delta
    public String Delta(String e,String x)throws IOException {
        String lin,el,sl,es;
        boolean trouve=false;
        es="";
        el="";
        sl="";
        BufferedReader fin =new BufferedReader(new FileReader(nomfich));
        StringTokenizer st;
        while(((lin=fin.readLine())!=null)&&(!(trouve))) {
            st = new StringTokenizer(lin," ?,.;:");
            if(st.hasMoreTokens())
                el=st.nextToken();
            if(el.equals(e)) {
                if(st.hasMoreTokens())
                    sl=st.nextToken();
                if(sl.equals(x)) {
                    if(st.hasMoreTokens())
                        es = st.nextToken();
                    trouve=true;
                }
            }
        }
        return es;
    }

    // fonction de verification
    public boolean verif(String ei,String w) {

        boolean v=false,x=true;
        String res="";
        try{
            if(w.length()==1) {
                res = Delta(ei, w);
                calculationSteps.add(printCalculationSteps(ei,w,res,"Ɛ"));
                x=false;
            }
            else {
                v = verif(Delta(ei, w.substring(0, 1)), w.substring(1));
                calculationSteps.add(printCalculationSteps(ei, w, Delta(ei, w.substring(0, 1)), w.substring(1)));
            }
        } catch(IOException e) {
            System.out.println(e);
        }
        int i=0;
        while((i<f.length)&&(!v)&&(!x)) {
            if(res.equals(f[i]))
                v=true;
            i++;
        }
        x=true;
        return v;
    }

    // affichage mathematique
    public StringBuilder printAutomate(){
        StringBuilder printedAutomate;
        printedAutomate = new StringBuilder("Automate M = ({");
        for (int i = 0; i < x.length; i++) {
            printedAutomate.append(x[i]);
            if (i+1<x.length){
                printedAutomate.append(",");
            }
        }
        printedAutomate.append("},{");
        for (int i = 0; i < q.length; i++) {
            printedAutomate.append(q[i]);
            if (i+1<q.length){
                printedAutomate.append(",");
            }
        }
        printedAutomate.append("},δ,").append(qi).append(",{");
        for (int i = 0; i < f.length; i++) {
            printedAutomate.append(f[i]);
            if (i+1<f.length){
                printedAutomate.append(",");
            }
        }
        printedAutomate.append("})");
        return printedAutomate;
    }
    // etape de calcul
    public String printCalculationSteps(String qd,String w,String qa,String w1){
        return "("+qd+","+w+")|--M--("+qa+","+w1+")";
    }
    public StringBuilder printCalculationSteps2(){
        StringBuilder result = new StringBuilder();
        //reverse claculaionSteps array
        ArrayList<String> revArrayList = new ArrayList<String>();
        for (int i = calculationSteps.size() - 1; i >= 0; i--) {
            revArrayList.add(calculationSteps.get(i));
        }
        for (String s : revArrayList){
            result.append(s);
            result.append("\n");
        }
        return result;
    }
    public boolean accepteLangageVide() throws IOException {
        ArrayList<String> etatsAccessible = new ArrayList<>(List.of(qi));
        int i = 0;
        while (i<etatsAccessible.size()){
            for (int j = 0; j < etatsAccessible.size(); j++) {
                for (int k = 0; k < x.length; k++) {
                    if (!etatsAccessible.contains(Delta(etatsAccessible.get(i),x[k]))){
                        etatsAccessible.add(Delta(etatsAccessible.get(i),x[k]));
                    }
                }
            }
            i++;
        }
        ArrayList<String> etatsFinaux = new ArrayList<>(List.of(f));
        for (int j = 0; j < etatsFinaux.size(); j++) {
            if (etatsAccessible.contains(etatsFinaux.get(j))){
                return false;
            }
        }
        return true;
    }
}

