package com.example.floydfx.classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GrafoFloyd {

    public final int INF = 999999; // valor para representar o infinito
    private String[] vertices; // vetor q guarda os vertices
    private int[][] dist; // matriz de distancia
    private int[][] sol; // matriz de solucao
    private int qtdVertices; // qtde de vertices
    private boolean carregado; // flag q serve para saber se o arquivo foi lido certo

    // Construtor que inicializa os atributos com valores padrao
    public GrafoFloyd() {
        this.qtdVertices = 0;
        this.carregado = false;
    }

    // le o arquivo txt e monta as matrizes iniciais
    public boolean carregarArquivo(String caminhoArquivo) {
        boolean sucesso = true;
        BufferedReader leitor = null;

        try {
            leitor = new BufferedReader(new FileReader(caminhoArquivo));
            String primeiraLinha = leitor.readLine();

            // verifica se a primeira linha existe e nao esta vazia
            if (primeiraLinha != null && primeiraLinha.length() > 0) {
                this.qtdVertices = contarVertices(primeiraLinha); // conta os vertices
                this.vertices = new String[this.qtdVertices]; // cria o vetor de vertices
                extrairRotulos(primeiraLinha); // pega os rotulos (vertices)
                this.dist = new int[this.qtdVertices][this.qtdVertices]; // cria a matriz de dist
                this.sol = new int[this.qtdVertices][this.qtdVertices]; // e a de solucao

                // laço p ler cada linha da matriz no arquivo
                for (int i = 0; i < this.qtdVertices; i++) {
                    String linhaMatriz = leitor.readLine();
                    if (linhaMatriz != null) {
                        // coloca valores na linha i das matrizes dist e sol
                        preencherLinhaMatriz(i, linhaMatriz);
                    } else {
                        sucesso = false; // erro
                    }
                }
            } else { // se o arquivo estiver vazio
                sucesso = false;
            }
        } catch (IOException e) { // erro de abrir arquivo
            sucesso = false;
        } finally {
            if (leitor != null) {
                try {
                    leitor.close();
                } catch (IOException e) {
                    sucesso = false;
                }
            }
        }

        this.carregado = sucesso;
        return sucesso; // retorna se deu certo ou n
    }

    // conta quantos vertices tem no cabecalho (primeira linha)
    private int contarVertices(String linha) {
        // comeca em 1
        int total = 1;
        for (int i = 0; i < linha.length(); i++) {
            if (linha.charAt(i) == '/') { // se encontrar o barra ele soma
                total = total + 1;
            }
        }
        return total;
    }

    // separa os nomes dos vertices (rotulos)
    private void extrairRotulos(String linha) {
        int indiceVertice = 0;
        String nomeAtual = "";

        // le toda a string do cabecalho
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '/') {
                this.vertices[indiceVertice] = nomeAtual; // salva o nome montado no vetor
                indiceVertice = indiceVertice + 1;
                nomeAtual = "";
            } else {
                if (c != ' ' && c != '\r' && c != '\n') { // ignora espacos em branco ou quebras de linha
                    nomeAtual = nomeAtual + c; // concatena os caracteres no rotulo
                }
            }
        }
        this.vertices[indiceVertice] = nomeAtual; // salva o ultimo rotulo
    }

    // le os valores de uma linha da matriz e preenche dist e sol
    private void preencherLinhaMatriz(int linhaIdx, String textoLinha) {
        int colIdx = 0;
        String token = "";
        int tamanho = textoLinha.length();

        // percorre a linha ate o fim incluindo uma verificacao extra no final
        for (int i = 0; i <= tamanho; i++) {
            char c = ' ';
            if (i < tamanho) {
                c = textoLinha.charAt(i);
            }

            // verifica se chegou em um separador de coluna
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                if (token.length() > 0 && colIdx < this.qtdVertices) {
                    this.dist[linhaIdx][colIdx] = converterPeso(linhaIdx, colIdx, token); // string p/ int
                    this.sol[linhaIdx][colIdx] = 0; // inicia a matriz de solucao em 0
                    colIdx = colIdx + 1;
                    token = "";
                }
            } else {
                token = token + c;
            }
        }
    }

    // interpreta o peso lido e trata ausencia de aresta como infinito
    private int converterPeso(int i, int j, String valor) {
        // resultado que sera retornado no final
        int peso = 0;

        // na diagonal principal a distancia de um vertice para ele mesmo eh zero
        if (i == j) {
            peso = 0;
        } else {
            // verifica ausencia de aresta
            if (valor.equals("-") || valor.equals("INF") || valor.equals("0")) {
                // coloca infinito quando nao ha aresta direta entre vertices distintos
                peso = INF;
            } else {
                // converte a string para inteiro
                peso = converterStringParaInt(valor);
            }
        }
        // retorna o peso ja convertido
        return peso;
    }

    // converte string numerica em int
    private int converterStringParaInt(String texto) {
        int numero = 0;
        int sinal = 1;
        int inicio = 0;

        // verifica se o primeiro caractere eh sinal de menos
        if (texto.charAt(0) == '-') {
            sinal = -1;
            inicio = 1;
        }

        // percorre os digitos da string
        for (int i = inicio; i < texto.length(); i++) {
            char digito = texto.charAt(i);
            numero = (numero * 10) + (digito - '0');
        }

        return numero * sinal;
    }

    // executa as V iteracoes do algoritmo de Floyd
    public void executarFloyd() {
        // verifica se existe um grafo carregado antes de executar ( o arquivo foi lido)
        if (this.carregado) {
            // laco externo do vertice k
            for (int k = 0; k < this.qtdVertices; k++) {
                // laco das linhas i representando a origem
                for (int i = 0; i < this.qtdVertices; i++) {
                    // laco das colunas j representando o destino
                    for (int j = 0; j < this.qtdVertices; j++) {

                        // verifica se existem caminhos validos passando por k
                        if (this.dist[i][k] != INF && this.dist[k][j] != INF) {

                            // calcula a distancia passando pelo vertice k
                            int novaDistancia = this.dist[i][k] + this.dist[k][j];

                            // compara se o novo caminho eh menor que o atual
                            if (this.dist[i][j] > novaDistancia) {
                                // atualiza a matriz de distancias com o menor valor
                                this.dist[i][j] = novaDistancia;
                                // registra k+1 na matriz solucao
                                this.sol[i][j] = k + 1;
                            }
                        }
                    }
                }
            }
        }
    }

    // busca o indice de um vertice pelo seu nome (exaustiva msm)
    public int buscarIndiceVertice(String nome) {
        int indiceEncontrado = -1;
        boolean achou = false;
        int i = 0;

        while (i < this.qtdVertices && !achou) {
            if (this.vertices[i].equals(nome)) {
                indiceEncontrado = i;
                achou = true;
            }
            i = i + 1;
        }

        return indiceEncontrado;
    }

    // monta a string completa do caminho minimo entre dois rotulos
    public String obterCaminhoMinimo(String nomeOrigem, String nomeDestino) {
        String resultado = ""; // texto final que sera devolvido
        int idxOrigem = buscarIndiceVertice(nomeOrigem);
        int idxDestino = buscarIndiceVertice(nomeDestino);

        // valida se ambos os vertices existem e se o grafo esta carregado
        if (this.carregado && idxOrigem != -1 && idxDestino != -1) {

            // verifica se existe caminho alcancavel entre eles
            if (this.dist[idxOrigem][idxDestino] == INF) {
                resultado = "Nao existe caminho entre " + nomeOrigem + " e " + nomeDestino;
            } else {
                // bbtem o custo total na matriz de distancias
                int custo = this.dist[idxOrigem][idxDestino];
                // reconstroi a sequencia de vertices usando a matriz solucao
                String rota = reconstruirRota(idxOrigem, idxDestino);
                resultado = "Custo: " + custo + " | Caminho: " + rota;
            }
        } else {
            resultado = "Vertices invalidos ou grafo nao carregado.";
        }

        return resultado;
    }

    // reconstroi o caminho/rota
    private String reconstruirRota(int i, int j) {
        String trecho = "";
        int intermediario = this.sol[i][j];

        // se for 0 a ligacao entre i e j é direta
        if (intermediario == 0) {
            // monta o passo direto usando os rotulos dos vertices
            trecho = this.vertices[i] + " -> " + this.vertices[j];
        } else {
            // converte o numero do vertice salvo (1..N) para indice de vetor (0..N-1)
            int k = intermediario - 1;
            // reconstroi a primeira metade de i ate k
            String primeiraParte = reconstruirRota(i, k);
            // reconstroi a segunda metade de k ate j
            String segundaParte = reconstruirRota(k, j);
            // remove a repeticao do vertice intermediario k na juncao das duas partes
            trecho = juntarTrechos(primeiraParte, segundaParte, this.vertices[k]);
        }

        return trecho;
    }

    // junta duas partes de rota sem duplicar o vertice do meio
    private String juntarTrechos(String parte1, String parte2, String verticeMeio) {
        // calcula quantos caracteres devem ser mantidos da primeira parte
        int corte = parte1.length() - verticeMeio.length();
        String inicio = "";

        // copia caractere por caractere ate onde deve ser o corte
        for (int i = 0; i < corte; i++) {
            inicio = inicio + parte1.charAt(i);
        }

        // eetorna a juncao limpa das duas partes
        return inicio + parte2;
    }

    public int[][] getDist() {
        return this.dist;
    }

    public int[][] getSol() {
        return this.sol;
    }

    public String[] getVertices() {
        return this.vertices;
    }

    public int getQtdVertices() {
        return this.qtdVertices;
    }
}