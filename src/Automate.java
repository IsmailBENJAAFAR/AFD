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
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Array;
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
    public boolean accepteLangageFini() throws IOException {
        int n = q.length;
        Automate automateTest = new Automate(this.nomfich);
        for (int i = n; i < ((2*n)-1); i++) {
            int n1 = (int) Math.pow(2,i);
            for (int j = 0; j < n1; j++) {
                String mot = StringUtils.leftPad(Integer.toBinaryString(j), i, '0');
                StringBuilder w = new StringBuilder();
                for (int k = 0; k < mot.length(); k++) {
                    w.append(x[Integer.parseInt(String.valueOf(mot.charAt(k)))]);
                }
                if(automateTest.verif(qi, String.valueOf(w))){
                    return false;
                }
            }
        }
        return true;
    }

    // Fusionne deux alphabets en supprimant les doublons
    /*
     * Méthode privée pour fusionner deux alphabets en éliminant les doublons.
     * Elle prend en paramètre deux tableaux de chaînes de caractères représentant les alphabets
     * des deux automates et retourne un tableau contenant l'union des deux alphabets sans doublons.
     */
    private String[] fusionnerAlphabet(String[] alphabet1, String[] alphabet2) {
        // Création d'un ensemble pour stocker les symboles de l'alphabet sans doublons
        Set<String> alphabetSet = new HashSet<>(Arrays.asList(alphabet1));
        // Ajout de tous les symboles de l'alphabet 2 à l'ensemble
        alphabetSet.addAll(Arrays.asList(alphabet2));
        // Conversion de l'ensemble en tableau et retour du résultat
        return alphabetSet.toArray(new String[0]);
    }


    // Combine les états des deux automates

    /*
     * Méthode privée pour combiner les états de deux automates en produisant tous les
     * états possibles dans le produit cartésien des deux ensembles d'états.
     * Elle prend en paramètre deux tableaux de chaînes de caractères représentant les ensembles
     * d'états des deux automates et retourne un tableau contenant tous les états combinés.
     */
    private String[] combinerEtats(String[] etats1, String[] etats2) {
        // Création d'une liste pour stocker les nouveaux états combinés
        List<String> nouveauxEtats = new ArrayList<>();
        // Parcours de tous les états de chaque automate et création des combinaisons
        for (String etat1 : etats1) {
            for (String etat2 : etats2) {
                // Ajout de la combinaison d'états à la liste des nouveaux états
                nouveauxEtats.add(etat1 + "-" + etat2);
            }
        }
        // Conversion de la liste en tableau et retour du résultat
        return nouveauxEtats.toArray(new String[0]);
    }


    // Combine les états finaux des deux automate
    /*
     * Méthode privée pour combiner les états finaux de deux automates en fusionnant les ensembles
     * d'états finaux de chaque automate. Elle élimine les doublons et retourne un tableau contenant
     * les états finaux combinés.
     */
    private String[] produitEtatsFinaux(String[] etatQ1,String[] etatsFinaux1,String[] etatQ2, String[] etatsFinaux2) {
        // Création d'une liste pour stocker les nouveaux états combinés
        List<String> nouveauxEtats = new ArrayList<>();
        //combiner etats F1xQ2
        String[] f1Q2 = combinerEtats(etatsFinaux1,etatQ2);
        //combiner etats Q1xF2
        String[] q1F2 = combinerEtats(etatQ1,etatsFinaux2);
        nouveauxEtats.addAll(List.of(f1Q2));
        nouveauxEtats.addAll(List.of(q1F2));
        return nouveauxEtats.toArray(new String[0]);
    }

    public Automate reunion(Automate a2) {
        try {
            String[] nouvelAlphabet = fusionnerAlphabet(this.x, a2.x);
            String[] nouveauxEtats = combinerEtats(this.q, a2.q);
            String nouvelEtatInitial = this.qi + "-" + a2.qi;
            String[] nouveauxEtatsFinaux = produitEtatsFinaux(this.q,this.f,a2.q, a2.f);

            // Construction de la nouvelle fonction de transition
            List<String> nouvelleFonctionTransition = new ArrayList<>();
            for (String etat : nouveauxEtats) {
                String[] etats = etat.split("-");
                for (String symbole : nouvelAlphabet) {
                    String nouvelEtat1 = this.Delta(etats[0], symbole);
                    String nouvelEtat2 = a2.Delta(etats[1], symbole);
                    String nouvelEtat = nouvelEtat1 + "-" + nouvelEtat2;
                    nouvelleFonctionTransition.add(etat + "," + symbole + "," + nouvelEtat);
                }
            }

            // Création du fichier pour le nouvel automate
            String nomNouveauFichier = "resultatDeReunion.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(nomNouveauFichier));
            writer.write(nouvelAlphabet.length + "," + nouveauxEtats.length + "," + nouveauxEtatsFinaux.length + "\n");
            for (int i = 0; i < nouvelAlphabet.length; i++) {
                writer.write(nouvelAlphabet[i]);
                if(i+1<nouvelAlphabet.length){
                    writer.write(",");
                }
            }
            writer.write("\n");
            for (int i = 0; i < nouveauxEtats.length; i++) {
                writer.write(nouveauxEtats[i]);
                if(i+1<nouveauxEtats.length){
                    writer.write(",");
                }
            }
            writer.write("\n");
            for (int i = 0; i < nouveauxEtatsFinaux.length; i++) {
                writer.write(nouveauxEtatsFinaux[i]);
                if(i+1<nouveauxEtatsFinaux.length){
                    writer.write(",");
                }
            }
            writer.write("\n");
            writer.write(nouvelEtatInitial + "\n");
            for (String transition : nouvelleFonctionTransition)
                writer.write(transition + "\n");
            writer.close();

            return new Automate(nomNouveauFichier);
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    // Opération d'intersection de deux automates
    public Automate intersection(Automate a2) {
        try {
            // Fusion des alphabets
            String[] nouvelAlphabet = fusionnerAlphabet(this.x, a2.x);

            // Combinaison des états
            String[] nouveauxEtats = combinerEtats(this.q, a2.q);

            // Création de l'état initial pour l'intersection
            String nouvelEtatInitial = this.qi +"-"+ a2.qi;

            // Fusion des états finaux
            String[] nouveauxEtatsFinaux = intersectionEtatsFinaux(this.f, a2.f);

            // Construction de la nouvelle fonction de transition
            List<String> nouvelleFonctionTransition = new ArrayList<>();
            for (String etat : nouveauxEtats) {
                String[] etats = etat.split("-");
                for (String symbole : nouvelAlphabet) {
                    String nouvelEtat1 = this.Delta(etats[0], symbole);
                    String nouvelEtat2 = a2.Delta(etats[1], symbole);
                    String nouvelEtat = nouvelEtat1 + "-" + nouvelEtat2;
                    nouvelleFonctionTransition.add(etat + "," + symbole + "," + nouvelEtat);
                }
            }

            // Création du fichier pour le nouvel automate
            String nomNouveauFichier = "resultatIntersection.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(nomNouveauFichier));
            writer.write(nouvelAlphabet.length + "," + nouveauxEtats.length + "," + nouveauxEtatsFinaux.length + "\n");
            for (int i = 0; i < nouvelAlphabet.length; i++) {
                writer.write(nouvelAlphabet[i]);
                if(i+1<nouvelAlphabet.length){
                    writer.write(",");
                }
            }
            writer.write("\n");
            for (int i = 0; i < nouveauxEtats.length; i++) {
                writer.write(nouveauxEtats[i]);
                if(i+1<nouveauxEtats.length){
                    writer.write(",");
                }
            }
            writer.write("\n");
            for (int i = 0; i < nouveauxEtatsFinaux.length; i++) {
                writer.write(nouveauxEtatsFinaux[i]);
                if(i+1<nouveauxEtatsFinaux.length){
                    writer.write(",");
                }
            }
            writer.write("\n");
            writer.write(nouvelEtatInitial + "\n");
            for (String transition : nouvelleFonctionTransition)
                writer.write(transition + "\n");
            writer.close();

            return new Automate(nomNouveauFichier);
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    // Fusionne les états finaux des deux automates
    private String[] intersectionEtatsFinaux(String[] etatsFinaux1, String[] etatsFinaux2) {
        // Création d'une liste pour stocker les nouveaux états combinés
        List<String> nouveauxEtats = new ArrayList<>();
        // Parcours de tous les états de chaque automate et création des combinaisons
        for (String etat1 : etatsFinaux1) {
            for (String etat2 : etatsFinaux2) {
                // Ajout de la combinaison d'états à la liste des nouveaux états
                nouveauxEtats.add(etat1 + "-" + etat2);
            }
        }
        // Conversion de la liste en tableau et retour du résultat
        return nouveauxEtats.toArray(new String[0]);
    }
    // Opération de différence de deux automates
    public Automate difference(Automate a2) {
        try {
            // Fusion des alphabets
            String[] nouvelAlphabet = fusionnerAlphabet(this.x, a2.x);

            // Combinaison des états
            String[] nouveauxEtats = combinerEtats(this.q, a2.q);

            // Création de l'état initial pour la différence
            String nouvelEtatInitial = this.qi +"-"+ a2.qi;

            // Calcul des états finaux de la différence
            String[] nouveauxEtatsFinaux = differenceEtatsFinaux(this.f,a2.q, a2.f);

            // Construction de la nouvelle fonction de transition
            List<String> nouvelleFonctionTransition = new ArrayList<>();
            for (String etat : nouveauxEtats) {
                String[] etats = etat.split("-");
                for (String symbole : nouvelAlphabet) {
                    String nouvelEtat1 = this.Delta(etats[0], symbole);
                    String nouvelEtat2 = a2.Delta(etats[1], symbole);
                    String nouvelEtat = nouvelEtat1 + "-" + nouvelEtat2;
                    nouvelleFonctionTransition.add(etat + "," + symbole + "," + nouvelEtat);
                }
            }

            // Création du fichier pour le nouvel automate
            String nomNouveauFichier = "resultatDifference.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(nomNouveauFichier));
            writer.write(nouvelAlphabet.length + "," + nouveauxEtats.length + "," + nouveauxEtatsFinaux.length + "\n");
            for (int i = 0; i < nouvelAlphabet.length; i++) {
                writer.write(nouvelAlphabet[i]);
                if(i+1<nouvelAlphabet.length){
                    writer.write(",");
                }
            }
            writer.write("\n");
            for (int i = 0; i < nouveauxEtats.length; i++) {
                writer.write(nouveauxEtats[i]);
                if(i+1<nouveauxEtats.length){
                    writer.write(",");
                }
            }
            writer.write("\n");
            for (int i = 0; i < nouveauxEtatsFinaux.length; i++) {
                writer.write(nouveauxEtatsFinaux[i]);
                if(i+1<nouveauxEtatsFinaux.length){
                    writer.write(",");
                }
            }
            writer.write("\n");
            writer.write(nouvelEtatInitial + "\n");
            for (String transition : nouvelleFonctionTransition)
                writer.write(transition + "\n");
            writer.close();

            return new Automate(nomNouveauFichier);
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    // Calcule les états finaux de la différence
    private String[] differenceEtatsFinaux(String[] etatsFinaux1,String[] etatQ2, String[] etatsFinaux2) {
        // Création d'une liste pour stocker les nouveaux états combinés
        List<String> nouveauxEtats = new ArrayList<>();
        //constructuion de nouveau ensemble
        ArrayList<String> q2F2 = new ArrayList<String>();
        ArrayList<String> eF2 = new ArrayList<String>(List.of(etatsFinaux2));
        for (String e : etatQ2) {
            if (!eF2.contains(e)){
                q2F2.add(e);
            }
        }
        // Parcours de tous les états de chaque automate et création des combinaisons
        for (String etat1 : etatsFinaux1) {
            for (String etat2 : q2F2) {
                // Ajout de la combinaison d'états à la liste des nouveaux états
                nouveauxEtats.add(etat1 + "-" + etat2);
            }
        }
        // Conversion de la liste en tableau et retour du résultat
        return nouveauxEtats.toArray(new String[0]);
    }

    // Opération de complémentaire de l'automate
    public Automate complement() {
        try {

            // Création de l'automate complémentaire avec les mêmes paramètres que l'automate d'origine
            Automate complementaire = new Automate(this.nomfich);
            // Calcul de l'ensemble des états finaux
            Set<String> etatsNonFinaux = new HashSet<>(Arrays.asList(this.q));
            etatsNonFinaux.removeAll(Arrays.asList(this.f));
            complementaire.f = etatsNonFinaux.toArray(new String[0]);

            // Calcul de la fonction de transition pour l'automate complémentaire
            List<String> nouvelleFonctionTransition = new ArrayList<>();
            for (String etat : this.q) {
                for (String symbole : this.x) {
                    String nouvelEtat = this.Delta(etat, symbole);
                    if (!nouvelleFonctionTransition.contains(etat + "," + symbole + "," + nouvelEtat)) {
                        nouvelleFonctionTransition.add(etat + "," + symbole + "," + nouvelEtat);
                    }
                }
            }

            // Génération du nom du fichier pour l'automate complémentaire
            String nomNouveauFichier = "complementaire_Automate1";
            BufferedWriter writer = new BufferedWriter(new FileWriter(nomNouveauFichier));

            // Écriture des informations de l'automate complémentaire dans le fichier
            writer.write(complementaire.x.length + "," + complementaire.q.length + "," + (complementaire.q.length-complementaire.f.length) + "\n");
            for (int i = 0; i < complementaire.x.length; i++) {
                writer.write(complementaire.x[i]);
                if(i+1<complementaire.x.length){
                    writer.write(",");
                }
            }
            writer.write("\n");
            for (int i = 0; i < complementaire.q.length; i++) {
                writer.write(complementaire.q[i]);
                if(i+1<complementaire.q.length){
                    writer.write(",");
                }
            }
            writer.write("\n");
            for (int i = 0; i < complementaire.f.length; i++) {
                writer.write(complementaire.f[i]);
                if(i+1<complementaire.f.length){
                    writer.write(",");
                }
            }
            writer.write("\n");
            writer.write(this.qi + "\n");
            for (String transition : nouvelleFonctionTransition)
                writer.write(transition + "\n");
            writer.close();

            return complementaire;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }





    /*public static void main(String[] args) {
        try {
            Automate a1 = new Automate("afd1.txt");
            Automate a2 = new Automate("afd2.txt");

            //Automate produitAutomate = a1.produit(a2);
            Automate reunionAutomate = a1.reunion(a2);
            Automate intersectionAutomate = a1.intersection(a2);
            Automate differenceAutomate = a1.difference(a2);
            Automate complementaire = a1.complement();

        } catch (IOException e) {
            System.out.println(e);
        }
    }*/
}

