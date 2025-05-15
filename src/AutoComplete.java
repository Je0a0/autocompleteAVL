package src;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

public class AutoComplete extends JFrame {
    private static final String ARQUIVO_DICIONARIO = "dicionario.txt";
    private ArvoreAVL arvore;
    private JTextField campoTexto;
    private JList<String> listaSugestoes;
    private DefaultListModel<String> modeloLista;

    public AutoComplete() {
        arvore = new ArvoreAVL();
        carregarDicionario();
        
        // Configuração da janela
        setTitle("Auto Complete com Árvore AVL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(500, 600);

        // Painel superior com campo de texto e botões
        JPanel painelSuperior = new JPanel(new BorderLayout());
        
        // Campo de texto
        campoTexto = new JTextField();
        campoTexto.setFont(new Font("Arial", Font.PLAIN, 16));
        painelSuperior.add(campoTexto, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        JButton btnInserir = new JButton("Inserir");
        JButton btnRemover = new JButton("Remover");
        JButton btnMostrarTodos = new JButton("Mostrar Todas");
        
        painelBotoes.add(btnInserir);
        painelBotoes.add(btnRemover);
        painelBotoes.add(btnMostrarTodos);
        
        painelSuperior.add(painelBotoes, BorderLayout.SOUTH);
        add(painelSuperior, BorderLayout.NORTH);

        // Lista de sugestões
        modeloLista = new DefaultListModel<>();
        listaSugestoes = new JList<>(modeloLista);
        listaSugestoes.setFont(new Font("Arial", Font.PLAIN, 14));
        add(new JScrollPane(listaSugestoes), BorderLayout.CENTER);

        // Adicionar listeners
        campoTexto.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { atualizarSugestoes(); }
            public void removeUpdate(DocumentEvent e) { atualizarSugestoes(); }
            public void changedUpdate(DocumentEvent e) { atualizarSugestoes(); }
        });

        listaSugestoes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selecao = listaSugestoes.getSelectedValue();
                    if (selecao != null) {
                        campoTexto.setText(selecao);
                    }
                }
            }
        });

        // Ações dos botões
        btnInserir.addActionListener(e -> {
            String palavra = campoTexto.getText().trim();
            if (!palavra.isEmpty()) {
                arvore.inserir(palavra);
                salvarDicionario();
                JOptionPane.showMessageDialog(this, "Palavra '" + palavra + "' inserida com sucesso!");
                campoTexto.setText("");
                atualizarSugestoes();
            }
        });

        btnRemover.addActionListener(e -> {
            String palavra = campoTexto.getText().trim();
            if (!palavra.isEmpty()) {
                arvore.remover(palavra);
                salvarDicionario();
                JOptionPane.showMessageDialog(this, "Palavra '" + palavra + "' removida com sucesso!");
                campoTexto.setText("");
                atualizarSugestoes();
            }
        });

        btnMostrarTodos.addActionListener(e -> {
            modeloLista.clear();
            List<String> todasPalavras = arvore.percursoEmOrdem();
            for (String palavra : todasPalavras) {
                modeloLista.addElement(palavra);
            }
        });

        // Adicionar painel de instruções
        JPanel painelInstrucoes = new JPanel();
        painelInstrucoes.setLayout(new BorderLayout());
        painelInstrucoes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel instrucoes = new JLabel("<html>" +
            "Digite para ver sugestões<br>" +
            "Clique duplo para selecionar uma palavra<br>" +
            "Use os botões para inserir ou remover palavras do dicionário" +
            "</html>");
        instrucoes.setFont(new Font("Arial", Font.PLAIN, 12));
        painelInstrucoes.add(instrucoes, BorderLayout.CENTER);
        
        add(painelInstrucoes, BorderLayout.SOUTH);

        setLocationRelativeTo(null);

        // Adicionar listener para salvar ao fechar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salvarDicionario();
            }
        });
    }

    private void carregarDicionario() {
        try {
            File arquivo = new File(ARQUIVO_DICIONARIO);
            if (arquivo.exists()) {
                arvore.carregarDeArquivo(ARQUIVO_DICIONARIO);
            } else {
                carregarPalavrasIniciais();
            }
        } catch (IOException e) {
            e.printStackTrace();
            carregarPalavrasIniciais();
        }
    }

    private void salvarDicionario() {
        try {
            arvore.salvarEmArquivo(ARQUIVO_DICIONARIO);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao salvar o dicionário: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarPalavrasIniciais() {
        String[] palavrasExemplo = {
            "casa", "carro", "cachorro", "cadeira", "caneta",
            "mesa", "mala", "computador", "telefone", "livro",
            "janela", "porta", "teclado", "mouse", "monitor"
        };

        for (String palavra : palavrasExemplo) {
            arvore.inserir(palavra);
        }
    }

    private void atualizarSugestoes() {
        SwingUtilities.invokeLater(() -> {
            String prefixo = campoTexto.getText().trim();
            modeloLista.clear();
            
            if (!prefixo.isEmpty()) {
                List<String> sugestoes = arvore.buscarSugestoes(prefixo);
                for (String sugestao : sugestoes) {
                    modeloLista.addElement(sugestao);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AutoComplete().setVisible(true);
        });
    }
} 