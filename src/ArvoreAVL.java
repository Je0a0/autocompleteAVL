package src;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class ArvoreAVL {
    private Node raiz;

    public ArvoreAVL() {
        this.raiz = null;
    }

    private int altura(Node node) {
        if (node == null) return 0;
        return node.altura;
    }

    private int getFatorBalanceamento(Node node) {
        if (node == null) return 0;
        return altura(node.esquerda) - altura(node.direita);
    }

    private Node rotacaoDireita(Node y) {
        Node x = y.esquerda;
        Node T2 = x.direita;

        x.direita = y;
        y.esquerda = T2;

        y.altura = Math.max(altura(y.esquerda), altura(y.direita)) + 1;
        x.altura = Math.max(altura(x.esquerda), altura(x.direita)) + 1;

        return x;
    }

    private Node rotacaoEsquerda(Node x) {
        Node y = x.direita;
        Node T2 = y.esquerda;

        y.esquerda = x;
        x.direita = T2;

        x.altura = Math.max(altura(x.esquerda), altura(x.direita)) + 1;
        y.altura = Math.max(altura(y.esquerda), altura(y.direita)) + 1;

        return y;
    }

    public void inserir(String palavra) {
        raiz = inserirRec(raiz, palavra);
    }

    private Node inserirRec(Node node, String palavra) {
        if (node == null) {
            return new Node(palavra);
        }

        int comparacao = palavra.compareTo(node.palavra);
        if (comparacao < 0) {
            node.esquerda = inserirRec(node.esquerda, palavra);
        } else if (comparacao > 0) {
            node.direita = inserirRec(node.direita, palavra);
        } else {
            return node;
        }

        node.altura = Math.max(altura(node.esquerda), altura(node.direita)) + 1;

        int fatorBalanceamento = getFatorBalanceamento(node);

        // Casos de rotação
        if (fatorBalanceamento > 1 && palavra.compareTo(node.esquerda.palavra) < 0) {
            return rotacaoDireita(node);
        }
        if (fatorBalanceamento < -1 && palavra.compareTo(node.direita.palavra) > 0) {
            return rotacaoEsquerda(node);
        }
        if (fatorBalanceamento > 1 && palavra.compareTo(node.esquerda.palavra) > 0) {
            node.esquerda = rotacaoEsquerda(node.esquerda);
            return rotacaoDireita(node);
        }
        if (fatorBalanceamento < -1 && palavra.compareTo(node.direita.palavra) < 0) {
            node.direita = rotacaoDireita(node.direita);
            return rotacaoEsquerda(node);
        }

        return node;
    }

    // Novo método para remover palavra
    public void remover(String palavra) {
        raiz = removerRec(raiz, palavra);
    }

    private Node removerRec(Node node, String palavra) {
        if (node == null) {
            return null;
        }

        int comparacao = palavra.compareTo(node.palavra);
        if (comparacao < 0) {
            node.esquerda = removerRec(node.esquerda, palavra);
        } else if (comparacao > 0) {
            node.direita = removerRec(node.direita, palavra);
        } else {
            // Nó com um ou nenhum filho
            if (node.esquerda == null || node.direita == null) {
                Node temp = null;
                if (temp == node.esquerda) {
                    temp = node.direita;
                } else {
                    temp = node.esquerda;
                }

                if (temp == null) {
                    temp = node;
                    node = null;
                } else {
                    node = temp;
                }
            } else {
                // Nó com dois filhos
                Node temp = encontrarMenorValor(node.direita);
                node.palavra = temp.palavra;
                node.direita = removerRec(node.direita, temp.palavra);
            }
        }

        if (node == null) {
            return null;
        }

        // Atualizar altura
        node.altura = Math.max(altura(node.esquerda), altura(node.direita)) + 1;

        // Verificar balanceamento
        int fatorBalanceamento = getFatorBalanceamento(node);

        // Casos de rotação
        if (fatorBalanceamento > 1 && getFatorBalanceamento(node.esquerda) >= 0) {
            return rotacaoDireita(node);
        }
        if (fatorBalanceamento > 1 && getFatorBalanceamento(node.esquerda) < 0) {
            node.esquerda = rotacaoEsquerda(node.esquerda);
            return rotacaoDireita(node);
        }
        if (fatorBalanceamento < -1 && getFatorBalanceamento(node.direita) <= 0) {
            return rotacaoEsquerda(node);
        }
        if (fatorBalanceamento < -1 && getFatorBalanceamento(node.direita) > 0) {
            node.direita = rotacaoDireita(node.direita);
            return rotacaoEsquerda(node);
        }

        return node;
    }

    private Node encontrarMenorValor(Node node) {
        Node atual = node;
        while (atual.esquerda != null) {
            atual = atual.esquerda;
        }
        return atual;
    }

    // Método para percurso em ordem
    public List<String> percursoEmOrdem() {
        List<String> resultado = new ArrayList<>();
        percursoEmOrdemRec(raiz, resultado);
        return resultado;
    }

    private void percursoEmOrdemRec(Node node, List<String> resultado) {
        if (node != null) {
            percursoEmOrdemRec(node.esquerda, resultado);
            resultado.add(node.palavra);
            percursoEmOrdemRec(node.direita, resultado);
        }
    }

    // Métodos para persistência em arquivo
    public void salvarEmArquivo(String nomeArquivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            List<String> palavras = percursoEmOrdem();
            for (String palavra : palavras) {
                writer.write(palavra);
                writer.newLine();
            }
        }
    }

    public void carregarDeArquivo(String nomeArquivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    inserir(linha.trim());
                }
            }
        }
    }

    public List<String> buscarSugestoes(String prefixo) {
        List<String> sugestoes = new ArrayList<>();
        buscarSugestoesRec(raiz, prefixo, sugestoes);
        return sugestoes;
    }

    private void buscarSugestoesRec(Node node, String prefixo, List<String> sugestoes) {
        if (node == null) return;

        if (node.palavra.startsWith(prefixo)) {
            sugestoes.add(node.palavra);
        }

        if (prefixo.compareTo(node.palavra) < 0) {
            buscarSugestoesRec(node.esquerda, prefixo, sugestoes);
        }
        
        buscarSugestoesRec(node.direita, prefixo, sugestoes);
    }
} 