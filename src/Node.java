package src;

// Classe que representa um nó na árvore AVL
public class Node {
    // Dados do nó
    String palavra;    // Palavra armazenada no nó
    Node esquerda;     // Referência para o filho esquerdo
    Node direita;      // Referência para o filho direito
    int altura;        // Altura do nó na árvore

    // Construtor que inicializa um novo nó
    public Node(String palavra) {
        this.palavra = palavra;
        this.altura = 1;        // Novo nó sempre começa com altura 1
        this.esquerda = null;   // Inicialmente sem filhos
        this.direita = null;
    }
} 