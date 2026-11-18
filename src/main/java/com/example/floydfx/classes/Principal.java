package com.example.floydfx.classes;

public class Principal {

    // serve p testar o funcionamento via console
    public static void main(String[] args) {

        // cria a instancia para o grafo
        GrafoFloyd grafo = new GrafoFloyd();

        // carrega o arquivo de entrada no formato do anexo
        boolean ok = grafo.carregarArquivo("TESTEMA.txt");

        // verifica se a leitura ocorreu sem problemas
        if (ok) {
            System.out.println("MATRIZES INICIAIS");
            exibirMatrizes(grafo);
            grafo.executarFloyd();

            // exibe as matrizes finais apos as V iteracoes
            System.out.println("\nMATRIZES FINAIS (FLOYD)");
            exibirMatrizes(grafo);

            // testa a consulta de caminho minimo entre pares de vertices
            System.out.println("\nCONSULTA DE CAMINHO MINIMO"); // no caso esse é o do pdf
            System.out.println("De A para C: " + grafo.obterCaminhoMinimo("A", "C"));
            System.out.println("De C para A: " + grafo.obterCaminhoMinimo("C", "A"));
        } else {
            System.out.println("Erro ao ler o arquivo TESTEMA.txt");
        }
    }

    // imprime a Matriz Distancia e a Matriz Solucao lado a lado
    public static void exibirMatrizes(GrafoFloyd grafo) {
        String[] vertices = grafo.getVertices();
        int[][] dist = grafo.getDist();
        int[][] sol = grafo.getSol();
        int n = grafo.getQtdVertices();

        System.out.println("Matriz Distancia\t\tMatriz Solucao");

        System.out.print("   ");
        for (int j = 0; j < n; j++) {
            System.out.print(vertices[j] + "\t");
        }
        System.out.print("\t   ");
        for (int j = 0; j < n; j++) {
            System.out.print(vertices[j] + "\t");
        }
        System.out.println();

        for (int i = 0; i < n; i++) {
            System.out.print(vertices[i] + "  ");
            for (int j = 0; j < n; j++) {
                if (dist[i][j] == grafo.INF) {
                    System.out.print("INF\t");
                } else {
                    System.out.print(dist[i][j] + "\t");
                }
            }

            System.out.print("\t" + vertices[i] + "  ");

            for (int j = 0; j < n; j++) {
                System.out.print(sol[i][j] + "\t");
            }
            System.out.println();
        }
    }
}