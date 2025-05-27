package src;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

// Classe que implementa uma árvore AVL para armazenar palavras
public class ArvoreAVL {
    private Node raiz;  // Nó raiz da árvore

    // Construtor inicializa uma árvore vazia
    public ArvoreAVL() {
        this.raiz = null;
    }

    // Métodos auxiliares para manipulação da árvore
    private int altura(Node node) {
        if (node == null) return 0;
        return node.altura;
    }

    private int getFatorBalanceamento(Node node) {
        if (node == null) return 0;
        return altura(node.esquerda) - altura(node.direita);
    }

    // Rotação simples à direita
    private Node rotacaoDireita(Node y) {
        Node x = y.esquerda;
        Node T2 = x.direita;

        x.direita = y;
        y.esquerda = T2;

        // Atualiza alturas
        y.altura = Math.max(altura(y.esquerda), altura(y.direita)) + 1;
        x.altura = Math.max(altura(x.esquerda), altura(x.direita)) + 1;

        return x;
    }

    // Rotação simples à esquerda
    private Node rotacaoEsquerda(Node x) {
        Node y = x.direita;
        Node T2 = y.esquerda;

        y.esquerda = x;
        x.direita = T2;

        // Atualiza alturas
        x.altura = Math.max(altura(x.esquerda), altura(x.direita)) + 1;
        y.altura = Math.max(altura(y.esquerda), altura(y.direita)) + 1;

        return y;
    }

    // Métodos públicos para manipulação da árvore
    public void inserir(String palavra) {
        raiz = inserirRec(raiz, palavra);
    }

    private Node inserirRec(Node node, String palavra) {
        // Caso base: árvore vazia ou chegou em uma folha
        if (node == null) {
            return new Node(palavra);
        }

        // Insere na subárvore apropriada
        int comparacao = palavra.compareTo(node.palavra);
        if (comparacao < 0) {
            node.esquerda = inserirRec(node.esquerda, palavra);
        } else if (comparacao > 0) {
            node.direita = inserirRec(node.direita, palavra);
        } else {
            return node;  // Palavra já existe
        }

        // Atualiza altura e verifica balanceamento
        node.altura = Math.max(altura(node.esquerda), altura(node.direita)) + 1;
        int fatorBalanceamento = getFatorBalanceamento(node);

        // Casos de rotação
        if (fatorBalanceamento > 1) {
            if (palavra.compareTo(node.esquerda.palavra) < 0) {
                return rotacaoDireita(node);  // Rotação simples direita
            } else {
                node.esquerda = rotacaoEsquerda(node.esquerda);  // Rotação dupla
                return rotacaoDireita(node);
            }
        }
        if (fatorBalanceamento < -1) {
            if (palavra.compareTo(node.direita.palavra) > 0) {
                return rotacaoEsquerda(node);  // Rotação simples esquerda
            } else {
                node.direita = rotacaoDireita(node.direita);  // Rotação dupla
                return rotacaoEsquerda(node);
            }
        }

        return node;
    }

    // Método para remover uma palavra
    public void remover(String palavra) {
        raiz = removerRec(raiz, palavra);
    }

    private Node removerRec(Node node, String palavra) {
        if (node == null) return null;

        // Busca a palavra na árvore
        int comparacao = palavra.compareTo(node.palavra);
        if (comparacao < 0) {
            node.esquerda = removerRec(node.esquerda, palavra);
        } else if (comparacao > 0) {
            node.direita = removerRec(node.direita, palavra);
        } else {
            // Encontrou o nó a ser removido
            if (node.esquerda == null) {
                return node.direita;
            } else if (node.direita == null) {
                return node.esquerda;
            } else {
                // Nó com dois filhos
                Node temp = encontrarMenorValor(node.direita);
                node.palavra = temp.palavra;
                node.direita = removerRec(node.direita, temp.palavra);
            }
        }

        // Atualiza altura e verifica balanceamento
        node.altura = Math.max(altura(node.esquerda), altura(node.direita)) + 1;
        int fatorBalanceamento = getFatorBalanceamento(node);

        // Rebalanceia a árvore se necessário
        if (fatorBalanceamento > 1) {
            if (getFatorBalanceamento(node.esquerda) >= 0) {
                return rotacaoDireita(node);
            } else {
                node.esquerda = rotacaoEsquerda(node.esquerda);
                return rotacaoDireita(node);
            }
        }
        if (fatorBalanceamento < -1) {
            if (getFatorBalanceamento(node.direita) <= 0) {
                return rotacaoEsquerda(node);
            } else {
                node.direita = rotacaoDireita(node.direita);
                return rotacaoEsquerda(node);
            }
        }

        return node;
    }

    // Método auxiliar para encontrar o menor valor em uma subárvore
    private Node encontrarMenorValor(Node node) {
        Node atual = node;
        while (atual.esquerda != null) {
            atual = atual.esquerda;
        }
        return atual;
    }

    // Método para percorrer a árvore em ordem
    public List<String> percursoEmOrdem() {
        List<String> resultado = new ArrayList<>();
        if (raiz != null) {  // Verifica se a árvore não está vazia
            percursoEmOrdemRec(raiz, resultado);
        }
        return resultado;
    }

    private void percursoEmOrdemRec(Node node, List<String> resultado) {
        if (node == null) return;
        
        // Primeiro percorre a subárvore esquerda
        if (node.esquerda != null) {
            percursoEmOrdemRec(node.esquerda, resultado);
        }
        
        // Adiciona a palavra atual
        resultado.add(node.palavra);
        
        // Por fim, percorre a subárvore direita
        if (node.direita != null) {
            percursoEmOrdemRec(node.direita, resultado);
        }
    }

    // Métodos para persistência em arquivo
    public void salvarEmArquivo(String nomeArquivo) throws IOException {
        // Primeiro, limpa o arquivo existente
        new FileWriter(nomeArquivo, false).close();
        
        // Depois, salva as palavras atuais
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, true))) {
            for (String palavra : percursoEmOrdem()) {
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

    // Método para buscar sugestões de palavras
    public List<String> buscarSugestoes(String prefixo) {
        List<String> sugestoes = new ArrayList<>();
        if (raiz != null && prefixo != null && !prefixo.isEmpty()) {
            buscarSugestoesRec(raiz, prefixo, sugestoes);
        }
        return sugestoes;
    }

    private void buscarSugestoesRec(Node node, String prefixo, List<String> sugestoes) {
        if (node == null) return;

        // Se a palavra atual começa com o prefixo, adiciona às sugestões
        if (node.palavra != null && node.palavra.startsWith(prefixo)) {
            sugestoes.add(node.palavra);
        }

        // Continua a busca nas subárvores apropriadas
        if (prefixo.compareTo(node.palavra) < 0 && node.esquerda != null) {
            buscarSugestoesRec(node.esquerda, prefixo, sugestoes);
        }
        if (node.direita != null) {
            buscarSugestoesRec(node.direita, prefixo, sugestoes);
        }
    }
} 