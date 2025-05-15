package src;

public class Node {
    String palavra;
    Node esquerda;
    Node direita;
    int altura;

    public Node(String palavra) {
        this.palavra = palavra;
        this.altura = 1;
        this.esquerda = null;
        this.direita = null;
    }
} 