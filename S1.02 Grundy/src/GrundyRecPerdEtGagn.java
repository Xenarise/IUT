import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Grundy game with AI for the machine (VERSION 2)
 * This program is version 2 of the Grundy game.
 * @author Lejas-Grenier Gabin, Payen Titouan
 */

class GrundyRecPerdEtGagn {
	/**
	 * The main game state represented as a list of integers.
	 */
	ArrayList<Integer> jeuA = new ArrayList<Integer>();
	
	/**
	 * A list of losing positions recorded during the game.
	 * Each position is represented as a list of integers.
	 */
	ArrayList<ArrayList<Integer>> posPerdantes = new ArrayList<ArrayList<Integer>>();
	
	/**
	 * A list of winning positions recorded during the game.
	 * Each position is represented as a list of integers.
	 */
	ArrayList<ArrayList<Integer>> posGagnantes = new ArrayList<ArrayList<Integer>>();
	
	
	/**
	 * An array defining the players in the game. 
	 * Example: {"IA", "Joueur"} represents the AI and the player.
	 */
	String[] personnePartie = {"IA","Joueur"};
	
	/**
	 * A counter variable used to count the number of calls to the `estGagnante` method.
	 */
	long cpt = 0;
	
    /**
     * Main method of the program.
     */
    void principal() {
        testJouerGagnant();
		testPremier();
		testSuivant();
		testAlterner();
		testEstGagnanteEfficacite();
		System.out.println("*************************Le Jeu de Grundy******************************");
		leJeu();
		
    }
	
	/**
     * Tests the efficiency of the estGagnante method.
     */
	void testEstGagnanteEfficacite(){
		System.out.println();
		System.out.println("=== TEST EFFICACITE : EST GAGNANTE ===");
		System.out.println();
		
		int n;
		int nbTests = 35;
		long[][] tab = new long[nbTests + 1][2];
		long t1, t2, diffT;
		
		n = 3; // taille allumette de départ
		
		for(int i = 0; i <= nbTests; i++){
			
			jeuA.clear();
			posPerdantes.clear();
			posGagnantes.clear();
			jeuA.add(n);
			
			cpt = 0; // on remet cpt a 0
			
			// calcul du temp
			t1 = System.nanoTime(); // temps depuis 1970 quelques chose 
			estGagnante(jeuA);
			t2 = System.nanoTime(); // temps apres l'execution du code 
			diffT = (t2 -t1); // soustraction qui donne le temp qu'a prit le code et 
			/*
			System.out.println("Tps =" + diffT + "ns"); // on a comme ça le temp en seconde, mais on peut diviser par  1 000 000 pour obtenir les millisecondes
			System.out.println("Tps =" + diffT/1000000.0 + "ms");
			*/
			
			//mettre les valeurs dans le tableau a haque tours de boucle 
			tab[i][0] = cpt;    // Nombre d'appels récursifs
			tab[i][1] = diffT;
			
			n++;
		}
		
		// Afficher les résultats sous forme d'un grand tableau
		System.out.println("\nRésultats des tests (compteur récursif et temps d'exécution) :");
		System.out.println("------------------------------------------------------------");
		System.out.println("Allumettes\tcpt\t\t\tTemps (ns)");
		for (int i = 0; i <= nbTests; i++) {
			System.out.println((3 + i) + "\t\t" + tab[i][0] + "\t\t\t" + tab[i][1]);
		}
		
		System.out.println("=== FIN DU TEST ===");
		System.out.println();
	}
	
	/**
     * Allows a user to play against the machine.
     */
	void leJeu() {
		boolean finPartie = false;

		// Initialiser un jeu de base (par exemple un seul tas de 10 allumettes)
		jeuA.clear();
		jeuA.add(10);
		System.out.println("Bienvenue dans le jeu de Grundy !");

		// Boucle principale de la partie
		while (!finPartie) {
			System.out.println("\nTas actuel : " + jeuA.toString());
			System.out.println("C'est au tour de " + personnePartie[0]);

			// Vérifie si des coups sont possibles
			if (estPossible(jeuA)) {
				if (personnePartie[0].equals("IA")) {
					System.out.println("L'IA réfléchit...");
					if (estGagnante(jeuA)) {
						jouerGagnant(jeuA);
						System.out.println("L'IA joue un coup gagnant : " + jeuA.toString());
					} else {
						// IA joue un coup aléatoire si elle ne peut pas garantir une victoire
						int tas = -1;
						int nbAllumettes = -1;
						int i = 0;
						while (i < jeuA.size()) {
							if (jeuA.get(i) > 2) {
								tas = i;
								nbAllumettes = 1; // Enlever une allumette comme coup simple
								i = jeuA.size();
							}
						}
						if (tas != -1) {
							enlever(jeuA, tas, nbAllumettes);
							System.out.println("L'IA joue un coup aléatoire : " + jeuA.toString());
						}
					}
				} else {
					// Tour du joueur humain
					boolean coupValide = false;
					while (!coupValide) {
						int tas = SimpleInput.getInt("Choisissez un tas (entre 0 et " + (jeuA.size() - 1) + "): "); // ligne 0 est compter comme la premiere ligne
						while(tas < 0 || tas > jeuA.size()-1 || jeuA.get(tas) < 2){
							System.out.println("Coup invalide, réessayez !");
							tas = SimpleInput.getInt("Choisissez un tas (entre 0 et " + (jeuA.size() - 1) + "): "); // ligne 0 est compter comme la premiere ligne
						}
						
						int nbAllumettes = SimpleInput.getInt("Nombre d'allumettes à retirer (entre 1 et " + (jeuA.get(tas) - 1) + "): ");
						while(nbAllumettes <= 0 || nbAllumettes >= jeuA.get(tas)){
							System.out.println("Coup invalide, réessayez !");
							nbAllumettes = SimpleInput.getInt("Nombre d'allumettes à retirer (entre 1 et " + (jeuA.get(tas) - 1) + "): "); // ligne 0 est compter comme la premiere ligne
						}
						// Vérifie la validité du coup
						if (2 * nbAllumettes != jeuA.get(tas)) { // 2* nbAl.. jeuA.get(tas) permet de voir pour les séparation de taille distinct
							enlever(jeuA, tas, nbAllumettes);
							coupValide = true;
						} else {
							System.out.println("Coup invalide, réessayez !");
						}
					}
					System.out.println("Vous avez joué : " + jeuA.toString());
				}

				// Alterner les joueurs
				alterner(personnePartie);
			} else {
				// Fin de la partie
				System.out.println("Fin de la partie !");
				System.out.println("Le gagnant de la partie est : " + personnePartie[1]);
				finPartie = true;
			}
		}
    }
    
	/**
	 * Switches the players who are playing, specifically between the AI and the player.
	 * @param personnePartie - array containing the list of players
	 * @return updated array with the next player's turn
	 */
	String[] alterner(String[] personnePartie){
		String temp = personnePartie[0];
		personnePartie[0] = personnePartie[1];
		personnePartie[1] = temp;
		return personnePartie;
	}
	
	/**
	 * Tests the alterner method.
	 */
	void testAlterner(){
		String[] testAlt = {"Joueur1","Joueur2"};
		
		System.out.println();
        System.out.println("*** testAlterner() ***");
		
		System.out.println(
		"Voici le Joueur qui joue :" + testAlt[0] + "\n" + 
		"et voici le joueur qui attend son tour :" + testAlt[1] + "\n" +
		"avant d'alterner \n");
		alterner(testAlt);
		System.out.println(
		"Et voici le Joueur qui joue :" + testAlt[0] + "\n" + 
		"et voici le joueur qui attend son tour : :" + testAlt[1] + "\n" +
		"apres avoir alterner");
	}
	
	/**
	 * Plays the winning move if one exists.
	 * @param jeu - the game board
	 * @return true if there is a winning move, false otherwise
	 */
    boolean jouerGagnant(ArrayList<Integer> jeu) {
	
        boolean gagnant = false;
		
        if (jeu == null) {
            System.err.println("jouerGagnant(): le paramètre jeu est null");
        } else {
            ArrayList<Integer> essai = new ArrayList<Integer>();
			
			// Une toute première décomposition est effectuée à partir de jeu.
			// Cette première décomposition du jeu est enregistrée dans essai.
			// ligne est le numéro de la case du tableau ArrayList (qui commence à zéro) qui
			// mémorise le tas (nbre d'allumettes) qui a été décomposé
            int ligne = premier(jeu, essai);
			
			// mise en oeuvre de la règle numéro2
			// Une situation (ou position) est dite gagnante pour la machine, s’il existe AU MOINS UNE décomposition
			// (c-à-d UNE action qui consiste à décomposer un tas en 2 tas inégaux) perdante pour l’adversaire. C'est
			// évidemment cette décomposition perdante qui sera choisie par la machine.
            while (ligne != -1 && !gagnant) {
				// estPerdante est récursif
                if (estPerdante(essai)) {
					// estPerdante (pour l'adversaire) à true ===> Bingo essai est la décomposition choisie par la machine qui est alors
					// certaine de gagner !!
                    jeu.clear();
                    gagnant = true;
					// essai est recopié dans jeu car essai est la nouvelle situation de jeu après que la machine ait joué (gagnant)
                    for (int i = 0; i < essai.size(); i++) {
                        jeu.add(essai.get(i));
                    }
                } else {
					// estPerdante à false ===> la machine essaye une autre décomposition en faisant appel à "suivant".
					// Si, après exécution de suivant, ligne est à (-1) alors il n'y a plus de décomposition possible à partir de jeu (et on sort du while).
					// En d'autres mots : la machine n'a PAS trouvé à partir de jeu UNE décomposition gagnante.
                    ligne = suivant(jeu, essai, ligne);
                }
            }
        }
		
        return gagnant;
    }
    
    /**
	 * Checks if the given game state is a known losing position.
	 * 
	 * @param jeu - The current game state as a list of integers.
	 * @return true if the game state matches a known losing position; false otherwise.
	 */
    boolean estConnuePerdante ( ArrayList<Integer> jeu ){
		
		ArrayList<Integer> normalisation = new ArrayList<Integer>();
		boolean ret = false;
		
		for ( int i = 0; i < jeu.size(); i++ ){
			if ( jeu.get(i)>2 ) {
				normalisation.add( jeu.get(i) );
			}
		}
		normalisation.sort(null);
		
		for ( int j = 0; j < posPerdantes.size(); j++ ){
			if ( posPerdantes.get(j).equals(normalisation) && ret==false ) {
				ret = true;
			}
		}
		
		return ret;
	}
	
	/**
	 * Checks if a configuration is known as winning.
	 * 
	 * @param jeu - The game board configuration.
	 * @return true if the configuration is already recorded as winning.
	 */
    boolean estConnueGagnante ( ArrayList<Integer> jeu ){
		
		ArrayList<Integer> normalisation = new ArrayList<Integer>();
		boolean ret = false;
		
		for ( int i = 0; i < jeu.size(); i++ ){
			if ( jeu.get(i)>2 ) {
				normalisation.add( jeu.get(i) );
			}
		}
		normalisation.sort(null);
		
		for ( int j = 0; j < posGagnantes.size(); j++ ){
			if ( posGagnantes.get(j).equals(normalisation) && ret == false) {
				ret = true;
			}
		}
		
		return ret;
	}
	
	/**
	 * RECURSIVE method that determines if the configuration (current game or test game) is losing.
	 * This method is used by the machine to determine if the opponent is guaranteed to lose.
	 * 
	 * @param jeu the current game board (the state of the game at a certain point during the match)
	 * @return true if the configuration (of the game) is losing, false otherwise
	 */
    boolean estPerdante(ArrayList<Integer> jeu) {
		ArrayList<Integer> perdant = new ArrayList<Integer>();
		ArrayList<Integer> gagnant = new ArrayList<Integer>();
		int pos = 0;
		int pos1 = 0;
        boolean ret = true; // par défaut la configuration est perdante
		
		if (estConnueGagnante(jeu)){
			ret = false;
		}
		else if(estConnuePerdante(jeu)){ // si estConnuePerdantes est true on ne fait aucun calcul
			ret = true;
		}else{
			
			if (jeu == null) {
				System.err.println("estPerdante(): le paramètre jeu est null");
			}
			
			else {
				// si il n'y a plus que des tas de 1 ou 2 allumettes dans le plateau de jeu
				// alors la situation est forcément perdante (ret=true) = FIN de la récursivité
				if ( !estPossible(jeu) ) {
					ret = true;
				}
				
				else {
					// création d'un jeu d'essais qui va examiner toutes les décompositions
					// possibles à partir de jeu
					ArrayList<Integer> essai = new ArrayList<Integer>(); // size = 0 !
					
					// toute première décomposition : enlever 1 allumette au premier tas qui possède
					// au moins 3 allumettes, ligne = -1 signifie qu'il n'y a plus de tas d'au moins 3 allumettes
					int ligne = premier(jeu, essai);
					
					while ( (ligne != -1) && ret) {
						cpt++;
						// mise en oeuvre de la règle numéro1
						// Une situation (ou position) est dite perdante si et seulement si TOUTES ses décompositions possibles
						// (c-à-d TOUTES les actions qui consistent à décomposer un tas en 2 tas inégaux) sont TOUTES gagnantes 
						// (pour l’adversaire).
						// L'appel à "estPerdante" est RECURSIF.
						
						// Si "estPerdante(essai)" est true c'est équivalent à "estGagnante" est false, la décomposition
						// essai n'est donc pas gagnante, on sort du while et on renvoie false.
						if (estPerdante(essai) == true) {
							
							// Si UNE SEULE décomposition (à partir du jeu) est perdante (pour l'adversaire) alors le jeu n'EST PAS perdant.
							// On renverra donc false : la situation (jeu) n'est PAS perdante.
							ret = false;
										
						} else {
							// génère la configuration d'essai suivante (c'est-à-dire UNE décomposition possible)
							// à partir du jeu, si ligne = -1 il n'y a plus de décomposition possible
							ligne = suivant(jeu, essai, ligne);
						}	
					}
					if (ret){
						for (int i=0; i<jeu.size(); i++){
							if ( jeu.get(i) > 2 ){
								perdant.add(jeu.get(i));
							}
						}
						perdant.sort(null);
						while (pos < posPerdantes.size() && posPerdantes.get(pos).toString().compareTo(perdant.toString()) < 0){
							pos++;
						}
						posPerdantes.add(pos, perdant);
					}else{
						for (int i=0; i<jeu.size(); i++){
							if ( jeu.get(i) > 2 ){
								gagnant.add(jeu.get(i));
							}
						}
						gagnant.sort(null);
						while (pos1 < posGagnantes.size() && posGagnantes.get(pos1).toString().compareTo(gagnant.toString()) < 0){
							pos1++;
						}
						posGagnantes.add(pos1, gagnant);
					}
				}
				
			}
		}
        return ret;
    }
	
	/**
	 * Indicates if the configuration is winning.
	 * Method that simply calls "estPerdante".
	 * 
	 * @param jeu the game board
	 * @return true if the configuration is winning, false otherwise
	 */
    boolean estGagnante(ArrayList<Integer> jeu) {
        boolean ret = false;
        if (jeu == null) {
            System.err.println("estGagnante(): le paramètre jeu est null");
        } else {
            ret = !estPerdante(jeu);
        }
        return ret;
    }

    /**
	 * Brief tests for the joueurGagnant() method.
	 */
    void testJouerGagnant() {
        System.out.println();
        System.out.println("*** testJouerGagnant() ***");

        System.out.println("Test des cas normaux");
        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(6);
        ArrayList<Integer> resJeu1 = new ArrayList<Integer>();
        resJeu1.add(4);
        resJeu1.add(2);
		
        testCasJouerGagnant(jeu1, resJeu1, true);
        
    }

    /**
	 * Test of a specific case for the jouerGagnant() method.
	 *
	 * @param jeu the game board
	 * @param resJeu the game board after making a winning move
	 * @param res the expected result of jouerGagnant
	 */
    void testCasJouerGagnant(ArrayList<Integer> jeu, ArrayList<Integer> resJeu, boolean res) {
        // Arrange
        System.out.print("jouerGagnant (" + jeu.toString() + ") : ");

        // Act
        boolean resExec = jouerGagnant(jeu);

        // Assert
        System.out.print(jeu.toString() + " " + resExec + " : ");
		boolean egaliteJeux = jeu.equals(resJeu);
        if (  egaliteJeux && (res == resExec) ) {
            System.out.println("OK\n");
        } else {
            System.err.println("ERREUR\n");
        }
    }	

    /**
	 * Splits the matches of a game row into two piles (1 row = 1 pile).
	 * The new pile is always placed at the end of the array.
	 * The pile being split is reduced by the number of matches removed.
	 * 
	 * @param jeu   array representing the matches per row
	 * @param ligne the pile (row) from which matches will be separated
	 * @param nb    the number of matches REMOVED from the pile (row) during the split
	 */
    void enlever ( ArrayList<Integer> jeu, int ligne, int nb ) {
		// traitement des erreurs
        if (jeu == null) {
            System.err.println("enlever() : le paramètre jeu est null");
        } else if (ligne >= jeu.size()) {
            System.err.println("enlever() : le numéro de ligne est trop grand");
        } else if (nb >= jeu.get(ligne)) {
            System.err.println("enlever() : le nb d'allumettes à retirer est trop grand");
        } else if (nb <= 0) {
            System.err.println("enlever() : le nb d'allumettes à retirer est trop petit");
        } else if (2 * nb == jeu.get(ligne)) {
            System.err.println("enlever() : le nb d'allumettes à retirer est la moitié");
        } else {
			// nouveau tas ajouté au jeu (nécessairement en fin de tableau)
			// ce nouveau tas contient le nbre d'allumettes retirées (nb) du tas à séparer			
            jeu.add(nb);
			// le tas restant possède "nb" allumettes en moins
            jeu.set ( ligne, (jeu.get(ligne) - nb) );
        }
    }

	/**
	 * Tests if it is possible to split one of the piles.
	 * 
	 * @param jeu the game board
	 * @return true if there is at least one pile with 3 or more matches, false otherwise
	 */
    boolean estPossible(ArrayList<Integer> jeu) {
        boolean ret = false;
        if (jeu == null) {
            System.err.println("estPossible(): le paramètre jeu est null");
        } else {
            int i = 0;
            while (i < jeu.size() && !ret) {
                if (jeu.get(i) > 2) {
                    ret = true;
                }
                i = i + 1;
            }
        }
        return ret;
    }

    /**
	 * Creates an initial test configuration from the game.
	 * 
	 * @param jeu the game board
	 * @param jeuEssai the new test configuration of the game
	 * @return the number of the pile split into two, or (-1) if there is no pile with at least 3 matches
	 */
    int premier(ArrayList<Integer> jeu, ArrayList<Integer> jeuEssai) {
	
        int numTas = -1; // pas de tas à séparer par défaut
		int i;
		
        if (jeu == null) {
            System.err.println("premier(): le paramètre jeu est null");
        } else if (!estPossible((jeu)) ){
            System.err.println("premier(): aucun tas n'est divisible");
        } else if (jeuEssai == null) {
            System.err.println("premier(): le paramètre jeuEssai est null");
        } else {
            // avant la copie du jeu dans jeuEssai il y a un reset de jeuEssai 
            jeuEssai.clear(); // size = 0
            i = 0;
			
			// recopie case par case de jeu dans jeuEssai
			// jeuEssai est le même que le jeu avant la première configuration d'essai
            while (i < jeu.size()) {
                jeuEssai.add(jeu.get(i));
                i = i + 1;
            }
			
            i = 0;
			// rechercher un tas d'allumettes d'au moins 3 allumettes dans le jeu
			// sinon numTas = -1
			boolean trouve = false;
            while ( (i < jeu.size()) && !trouve) {
				
				// si on trouve un tas d'au moins 3 allumettes
				if ( jeuEssai.get(i) >= 3 ) {
					trouve = true;
					numTas = i;
				}
				
				i = i + 1;
            }
			
			// sépare le tas (case numTas) en un nouveau tas d'UNE SEULE allumette qui vient se placer en fin du tableau 
			// le tas en case numTas a diminué d'une allumette (retrait d'une allumette)
			// jeuEssai est le plateau de jeu qui fait apparaître cette séparation
            if ( numTas != -1 ) enlever ( jeuEssai, numTas, 1 );
        }
		
        return numTas;
    }

    /**
	 * Brief tests for the premier() method.
	 */
    void testPremier() {
        System.out.println();
        System.out.println("*** testPremier()");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(10);
        jeu1.add(11);
        int ligne1 = 0;
        ArrayList<Integer> res1 = new ArrayList<Integer>();
        res1.add(9);
        res1.add(11);
        res1.add(1);
        testCasPremier(jeu1, ligne1, res1);
    }

    /**
	 * Tests a specific case for the testPremier method.
	 * 
	 * @param jeu the game board
	 * @param ligne the number of the pile split first
	 * @param res the game board after the first split
	 */
    void testCasPremier(ArrayList<Integer> jeu, int ligne, ArrayList<Integer> res) {
        // Arrange
        System.out.print("premier (" + jeu.toString() + ") : ");
        ArrayList<Integer> jeuEssai = new ArrayList<Integer>();
        // Act
        int noLigne = premier(jeu, jeuEssai);
        // Assert
        System.out.println("\nnoLigne = " + noLigne + " jeuEssai = " + jeuEssai.toString());
		boolean egaliteJeux = jeuEssai.equals(res);
        if ( egaliteJeux && noLigne == ligne ) {
            System.out.println("OK\n");
        } else {
            System.err.println("ERREUR\n");
        }
    }

    /**
	 * Generates the next test configuration (i.e., ONE possible decomposition).
	 * 
	 * @param jeu the game board
	 * @param jeuEssai the test configuration of the game after the split
	 * @param ligne the number of the pile that was last split
	 * @return the number of the pile split into two for the new configuration, or -1 if no further decomposition is possible
	 */
    int suivant(ArrayList<Integer> jeu, ArrayList<Integer> jeuEssai, int ligne) {
	
        // System.out.println("suivant(" + jeu.toString() + ", " +jeuEssai.toString() +
        // ", " + ligne + ") = ");
		
		int numTas = -1; // par défaut il n'y a plus de décomposition possible
		
        int i = 0;
		// traitement des erreurs
        if (jeu == null) {
            System.err.println("suivant(): le paramètre jeu est null");
        } else if (jeuEssai == null) {
            System.err.println("suivant() : le paramètre jeuEssai est null");
        } else if (ligne >= jeu.size()) {
            System.err.println("suivant(): le paramètre ligne est trop grand");
        }
		
		else {
		
			int nbAllumEnLigne = jeuEssai.get(ligne);
			int nbAllDernCase = jeuEssai.get(jeuEssai.size() - 1);
			
			// si sur la même ligne (passée en paramètre) on peut encore retirer des allumettes,
			// c-à-d si l'écart entre le nombre d'allumettes sur cette ligne et
			// le nombre d'allumettes en fin de tableau est > 2, alors on retire encore
			// 1 allumette sur cette ligne et on ajoute 1 allumette en dernière case		
            if ( (nbAllumEnLigne - nbAllDernCase) > 2 ) {
                jeuEssai.set ( ligne, (nbAllumEnLigne - 1) );
                jeuEssai.set ( jeuEssai.size() - 1, (nbAllDernCase + 1) );
                numTas = ligne;
            } 
			
			// sinon il faut examiner le tas (ligne) suivant du jeu pour éventuellement le décomposer
			// on recrée une nouvelle configuration d'essai identique au plateau de jeu
			else {
                // copie du jeu dans JeuEssai
                jeuEssai.clear();
                for (i = 0; i < jeu.size(); i++) {
                    jeuEssai.add(jeu.get(i));
                }
				
                boolean separation = false;
                i = ligne + 1; // tas suivant
				// si il y a encore un tas et qu'il contient au moins 3 allumettes
				// alors on effectue une première séparation en enlevant 1 allumette
                while ( i < jeuEssai.size() && !separation ) {
					// le tas doit faire minimum 3 allumettes
                    if ( jeu.get(i) > 2 ) {
                        separation = true;
						// on commence par enlever 1 allumette à ce tas
                        enlever(jeuEssai, i, 1);
						numTas = i;
                    } else {
                        i = i + 1;
                    }
                }				
            }
        }
		
        return numTas;
    }

    /**
	 * Brief tests for the suivant() method.
	 */
    void testSuivant() {
        System.out.println();
        System.out.println("*** testSuivant() ****");

        int ligne1 = 0;
        int resLigne1 = 0;
        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(10);
        ArrayList<Integer> jeuEssai1 = new ArrayList<Integer>();
        jeuEssai1.add(9);
        jeuEssai1.add(1);
        ArrayList<Integer> res1 = new ArrayList<Integer>();
        res1.add(8);
        res1.add(2);
        testCasSuivant(jeu1, jeuEssai1, ligne1, res1, resLigne1);

        int ligne2 = 0;
        int resLigne2 = -1;
        ArrayList<Integer> jeu2 = new ArrayList<Integer>();
        jeu2.add(10);
        ArrayList<Integer> jeuEssai2 = new ArrayList<Integer>();
        jeuEssai2.add(6);
        jeuEssai2.add(4);
        ArrayList<Integer> res2 = new ArrayList<Integer>();
        res2.add(10);
        testCasSuivant(jeu2, jeuEssai2, ligne2, res2, resLigne2);

        int ligne3 = 1;
        int resLigne3 = 1;
        ArrayList<Integer> jeu3 = new ArrayList<Integer>();
        jeu3.add(4);
        jeu3.add(6);
        jeu3.add(3);
        ArrayList<Integer> jeuEssai3 = new ArrayList<Integer>();
        jeuEssai3.add(4);
        jeuEssai3.add(5);
        jeuEssai3.add(3);
        jeuEssai3.add(1);
        ArrayList<Integer> res3 = new ArrayList<Integer>();
        res3.add(4);
        res3.add(4);
        res3.add(3);
        res3.add(2);
        testCasSuivant(jeu3, jeuEssai3, ligne3, res3, resLigne3);

    }

    /**
	 * Tests a specific case for the suivant method.
	 * 
	 * @param jeu the game board
	 * @param jeuEssai the game board obtained after splitting a pile
	 * @param ligne the number of the pile that was last split
	 * @param resJeu the expected test game board after the split
	 * @param resLigne the expected number of the pile that was split
	 */
    void testCasSuivant(ArrayList<Integer> jeu, ArrayList<Integer> jeuEssai, int ligne, ArrayList<Integer> resJeu, int resLigne) {
        // Arrange
        System.out.print("suivant (" + jeu.toString() + ", " + jeuEssai.toString() + ", " + ligne + ") : ");
        // Act
        int noLigne = suivant(jeu, jeuEssai, ligne);
        // Assert
        System.out.println("\nnoLigne = " + noLigne + " jeuEssai = " + jeuEssai.toString());
		boolean egaliteJeux = jeuEssai.equals(resJeu);
        if ( egaliteJeux && noLigne == resLigne ) {
            System.out.println("OK\n");
        } else {
            System.err.println("ERREUR\n");
        }
    }

}
