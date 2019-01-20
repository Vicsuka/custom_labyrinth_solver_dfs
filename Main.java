import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {

    //feladatban ertekek
    public static final int NORTH = 1;
    public static final int EAST = 2;
    public static final int SOUTH = 4;
    public static final int WEST = 8;
    public static final int OBJECT = 16;

    // osszes objektiv szam
    private static int objectiveCount;

    //palya merete
    private static int mazeSize;

    // az osszes node sorban, matrix alakban
    private static Node[][] graph;

    public static void main(String[] args) {
        //System.out.println("Reading maze: ");
        int[][] maze = readMaze();
        Node[][] matrix = new Node[mazeSize][mazeSize];

        //Int tombbol -> Node tombb vagyis matrix elkeszitese
        for( int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++){
                matrix[i][j] = new Node(i,j,maze[i][j]);
                //System.out.println("Node at : "+ i + ":" + j + " top:  "+ matrix[i][j].top +" right:  "+matrix[i][j].right + " bottom:  "+ matrix[i][j].bottom +" left:  "+matrix[i][j].left +" EXIT?  "+ matrix[i][j].exitNode );
            }
        }
        graph = matrix;

        //System.out.println(objectiveCount);

        /*
        Node testNode = graph [0][2];
        ArrayList<Node> path = new ArrayList<Node>();
        path.add(findNext(testNode));
        path.get(0).getPosition();
        */

        //arraylistben eltaroljuk az utvonalat amikben nodeok lesznek.
        ArrayList<Node> path = new ArrayList<Node>();
        //kezdopozicio
        path.add(graph[0][0]);

        //addig menjen ameg mindent fel nem szedtunk
        while (objectiveCount > -100) {

            //Ameg van targy a labirintusban azt kell eloszor felszedni utana odamenni a kilepo node-ra
            Node targetNode = null;
            if (objectiveCount != 0) {
                for (int i = 0; i < mazeSize; i++) {
                    for (int j = 0; j < mazeSize; j++) {
                        if (matrix[i][j].object) {
                            targetNode = matrix[i][j];
                        }
                    }
                }
            } else {
                for( int i = 0; i < mazeSize; i++) {
                    for (int j = 0; j < mazeSize; j++){
                        if (graph[i][j].exitNode) {
                            targetNode = graph[i][j];
                        }
                    }
                }
            }

           /* System.out.println("Target located at: ");
            targetNode.getPosition();*/

           //Addig menjen az algoritmus ameg nincs az utvonalunkban a celnode
            while (!(path.contains(targetNode))) {
                /*for (int i = 0; i < path.size(); i++) {
                    path.get(i).getPosition();
                }
                System.out.println("------------------------------------");*/


                if (!path.isEmpty()) {
                    //Ha van lehetosegunk tovabblepni
                    if (findNext(path.get(path.size() - 1)) != null) {

                        // Ralepunk a kovetkezo nodera es beallitjuk hogy azt mar meglatogattuk
                        path.add(findNext(path.get(path.size() - 1)));
                        //System.out.println("added: "+ path.get(path.size() - 1).i + " " + path.get(path.size() - 1).j);
                        graph[path.get(path.size() - 1).i][path.get(path.size() - 1).j].visited = true;
                    } else {

                        //Ha nincs lehetosegunk tovabblepni muszaj egyet visszalepni mert zsakutca
                        path.remove(path.size() - 1);
                    }
                }
            }

            //Ha van utvonal akkor azt kiirjuk
            //System.out.println("Path found: ");
            for (int i = 1; i < path.size(); i++) {
                path.get(i).getPosition();
            }

            /*ha ralepunk egy objektiv node-ra akkor ki kell irni hogy felvesz viszont lehet
                 hogy az exiten van objektiv illetve csak siman exithez erhettunk
                 ekkor ki kell lepni az egesz ciklusbol mert vege
                 a path.get(path.size()-1) az mindenhol a legutoljara hozzaadott node ami a celnode
             */
            if (!graph[path.get(path.size() - 1).i][path.get(path.size() - 1).j].exitNode) {
                graph[path.get(path.size() - 1).i][path.get(path.size() - 1).j].object = false;
                graph[path.get(path.size() - 1).i][path.get(path.size() - 1).j].Take();
            } else {
                if (objectiveCount > 0) {
                    graph[path.get(path.size() - 1).i][path.get(path.size() - 1).j].object = false;
                    graph[path.get(path.size() - 1).i][path.get(path.size() - 1).j].Take();
                } else {
                    objectiveCount = -500;
                }
            }

            //uj utvonalat kell majd kereseni raadasul onnan ahol eppen allunk
            path.clear();
            path.add(targetNode);

            //uj utvonalat fogunk keresni szoval meg semmit nem latogattunk
            for( int i = 0; i < mazeSize; i++) {
                for (int j = 0; j < mazeSize; j++){
                    graph[i][j].visited = false;
                }
            }

            //megtalaltunk egy objektivet
            objectiveCount -= 1;
        }
        System.out.println();


    }


    //visszaad egy kovetkezo node-ot amire lephetunk es meg nem voltunk rajta ezen az utvonalon ha nincs akkor null
    public static Node findNext(Node node) {
        Node nextNode = null;

        Node rightNode = new Node(0,0,0);
        Node leftNode = new Node(0,0,0);
        Node bottomNode = new Node(0,0,0);
        Node topNode = new Node(0,0,0);


        // Szomszedos node-ok definialasa amennyiben leteznek.
        if (node.j < mazeSize-1)
            rightNode = graph[node.i][node.j+1];
        if (node.j > 0)
            leftNode = graph[node.i][node.j-1];
        if (node.i > 0)
            topNode = graph[node.i-1][node.j];
        if (node.i < mazeSize-1)
            bottomNode = graph[node.i+1][node.j];

        /* A kovetkezo node nem lehet olyan iranyban amerre nincs is mar palya
            ezert mindenhol meg kell vizsgalni ha pl jobb felso sarokban van akkor
            alapbol csak balra vagy lefele mehet.
         */
        if (node.i == 0) {
            if (node.j == 0) {
                if (!node.right) {
                    if (!rightNode.visited) {
                        nextNode = rightNode;
                    }
                }

                if (!node.bottom) {
                    if (!bottomNode.visited) {
                        nextNode = bottomNode;
                    }
                }
            } else

            if(node.j == mazeSize-1) {
                if (!node.left) {
                    if (!leftNode.visited) {
                        nextNode = leftNode;
                    }
                }

                if (!node.bottom) {
                    if (!bottomNode.visited) {
                        nextNode = bottomNode;
                    }
                }
            } else {
                if (!node.left) {
                    if (!leftNode.visited) {
                        nextNode = leftNode;
                    }
                }

                if (!node.bottom) {
                    if (!bottomNode.visited) {
                        nextNode = bottomNode;
                    }
                }

                if (!node.right) {
                    if (!rightNode.visited) {
                        nextNode = rightNode;
                    }
                }
            }
        } else

        if (node.j == 0) {
            if(node.i == mazeSize-1) {
                if (!node.top) {
                    if (!topNode.visited) {
                        nextNode = topNode;
                    }
                }

                if (!node.right) {
                    if (!rightNode.visited) {
                        nextNode = rightNode;
                    }
                }
            } else {
                if (!node.top) {
                    if (!topNode.visited) {
                        nextNode = topNode;
                    }
                }

                if (!node.bottom) {
                    if (!bottomNode.visited) {
                        nextNode = bottomNode;
                    }
                }

                if (!node.right) {
                    if (!rightNode.visited) {
                        nextNode = rightNode;
                    }
                }
            }
        } else

        if (node.i == mazeSize-1) {
            if (node.j == mazeSize-1) {
                if (!node.top) {
                    if (!topNode.visited) {
                        nextNode = topNode;
                    }
                }

                if (!node.left) {
                    if (!leftNode.visited) {
                        nextNode = leftNode;
                    }
                }
            } else {
                if (!node.top) {
                    if (!topNode.visited) {
                        nextNode = topNode;
                    }
                }

                if (!node.left) {
                    if (!leftNode.visited) {
                        nextNode = leftNode;
                    }
                }

                if (!node.right) {
                    if (!rightNode.visited) {
                        nextNode = rightNode;
                    }
                }
            }
        } else

        if (node.j == mazeSize-1) {
            if (!node.top) {
                if (!topNode.visited) {
                    nextNode = topNode;
                }
            }

            if (!node.bottom) {
                if (!bottomNode.visited) {
                    nextNode = bottomNode;
                }
            }

            if (!node.left) {
                if (!leftNode.visited) {
                    nextNode = leftNode;
                }
            }
        } else {
            if (!node.top) {
                if (!topNode.visited) {
                    nextNode = topNode;
                }
            }

            if (!node.bottom) {
                if (!bottomNode.visited) {
                    nextNode = bottomNode;
                }
            }

            if (!node.left) {
                if (!leftNode.visited) {
                    nextNode = leftNode;
                }
            }

            if (!node.right) {
                if (!rightNode.visited) {
                    nextNode = rightNode;
                }
            }

        }

        //ha barhova tovabb tud menni akkor az elso ilyen, egyebkent null
        return nextNode;
    }

    public static class Node {

        //meglatogattuk-e mar, kilepo node-e
        protected boolean visited;
        protected boolean exitNode;

        protected int i;
        protected int j;

        //van-e rajta item
        protected boolean object;

        //merre nem tud tovabbmenni, ha 1 akkor blokkolt
        protected boolean top;
        protected boolean right;
        protected boolean bottom;
        protected boolean left;

        //pozicio
        public void getPosition() {
            System.out.println(i + " " + j);
        }

        //felvenni
        public void Take() {
            System.out.println("felvesz");
        }


        //node elkeszitese
        Node(int i1, int j1, int k) {
            exitNode = false;
            visited = false;
            i = i1;
            j = j1;

            //objektiv van-e rajta??
            if (k >= OBJECT) {
                object = true;
                k = k - OBJECT;
            } else {
                object = false;
            }

            //nyugati fal??
            if (k >= WEST) {
                left = true;
                k = k - WEST;
            } else {
                left = false;
            }

            //nyugati fal??
            if (k >= SOUTH) {
                bottom = true;
                k = k - SOUTH;
            } else {
                bottom = false;
            }

            //nyugati fal??
            if (k >= EAST) {
                right = true;
                k = k - EAST;
            } else {
                right = false;
            }

            //nyugati fal??
            if (k >= NORTH) {
                top = true;
                k = k - NORTH;
            } else {
                top = false;
            }

            //kilepo node-e felul??
            if (i == 0) {
                if (j != 0) {
                    if (!top) {
                        exitNode = true;
                    }
                }
            }

            //kilepo node-e balra??
            if (j == 0) {
                if (i != 0) {
                    if (!left) {
                        exitNode = true;
                    }
                }
            }

            //kilepo node-e alul??
            if (i == mazeSize-1) {
                if (!bottom) {
                    exitNode = true;
                }
            }

            //kilepo node-e jobbra??
            if (j == mazeSize-1) {
                if (!right) {
                    exitNode = true;
                }
            }

        }
    }

    public static int [][] readMaze() {
        int [][] maze = new int [0][0];

        try {
            // Elso sor beolvasasa, szetdarabolasa ezek alapjan meret kideritese
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            String [] parts = line.split("\\s+");
            int N = parts.length;
            mazeSize = N;
            maze = new int[N][N];

            // Az elsotol az utolso elotti sorig feltoltes
            for( int i = 0; i < N-1; i++) {
                for (int j = 0; j < N; j++) {
                    maze[i][j] =Integer.parseInt(parts[j]);
                }
                line = br.readLine();
                parts = line.split("\\s+");
            }

            // Kompenzacio az utolagos beolvasas miatt
            for (int j = 0; j < N; j++) {
                maze[N-1][j] =Integer.parseInt(parts[j]);
            }

            // Objektivek szama
            line = br.readLine();
            objectiveCount = Integer.parseInt(line);

            isr.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return maze;
    }

}



