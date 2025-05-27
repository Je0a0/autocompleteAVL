package src;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

// Classe principal que implementa a interface gráfica do autocompletar
public class AutoComplete extends JFrame {
    // Constantes e componentes da interface
    private static final String ARQUIVO_DICIONARIO = "dicionario.txt";
    private ArvoreAVL arvore;
    private JTextField campoTexto;
    private JList<String> listaSugestoes;
    private DefaultListModel<String> modeloLista;
    private JButton btnInserir;
    private JButton btnRemover;
    private JButton btnMostrarTodas;

    // Construtor inicializa a interface
    public AutoComplete() {
        // Inicializa a árvore e carrega o dicionário
        arvore = new ArvoreAVL();
        carregarDicionario();

        configurarJanela();

        criarComponentes();

        adicionarListeners();

        // Centraliza a janela na tela
        setLocationRelativeTo(null);
    }

    // Configura as propriedades básicas da janela
    private void configurarJanela() {
        setTitle("Auto Complete com Árvore AVL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(500, 600);
    }

    // Cria e configura todos os componentes da interface
    private void criarComponentes() {
        // Painel superior com campo de texto e botões
        JPanel painelSuperior = criarPainelSuperior();
        add(painelSuperior, BorderLayout.NORTH);

        // Lista de sugestões
        criarListaSugestoes();
        add(new JScrollPane(listaSugestoes), BorderLayout.CENTER);

        // Painel de instruções
        criarPainelInstrucoes();
    }

    // Cria o painel superior com campo de texto e botões
    private JPanel criarPainelSuperior() {
        JPanel painelSuperior = new JPanel(new BorderLayout());

        // Campo de texto
        campoTexto = new JTextField();
        campoTexto.setFont(new Font("Arial", Font.PLAIN, 16));
        painelSuperior.add(campoTexto, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        btnInserir = new JButton("Inserir");
        btnRemover = new JButton("Remover");
        btnMostrarTodas = new JButton("Mostrar Todas");

        painelBotoes.add(btnInserir);
        painelBotoes.add(btnRemover);
        painelBotoes.add(btnMostrarTodas);

        painelSuperior.add(painelBotoes, BorderLayout.SOUTH);
        return painelSuperior;
    }

    // Cria a lista de sugestões
    private void criarListaSugestoes() {
        modeloLista = new DefaultListModel<>();
        listaSugestoes = new JList<>(modeloLista);
        listaSugestoes.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    // Cria o painel de instruções
    private void criarPainelInstrucoes() {
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
    }

    // Adiciona todos os listeners de eventos
    private void adicionarListeners() {
        // Listener para o campo de texto
        campoTexto.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { atualizarSugestoes(); }
            public void removeUpdate(DocumentEvent e) { atualizarSugestoes(); }
            public void changedUpdate(DocumentEvent e) { atualizarSugestoes(); }
        });

        // Listener para clique duplo na lista
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

        btnMostrarTodas.addActionListener(e -> {
            // Executa em uma thread separada para não travar a interface
            new Thread(() -> {
                try {
                    // Realiza a operação pesada na thread de background
                    List<String> todasPalavras = arvore.percursoEmOrdem();

                    // Atualiza a UI na EDT apenas quando os dados estiverem prontos
                    SwingUtilities.invokeLater(() -> {
                        try {
                            modeloLista.clear();
                            for (String palavra : todasPalavras) {
                                modeloLista.addElement(palavra);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this,
                                "Erro ao carregar palavras: " + ex.getMessage(),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                            "Erro ao processar palavras: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
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

    // Carrega o dicionário do arquivo
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

    // Salva o dicionário no arquivo
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

    // Carrega palavras iniciais caso o arquivo não exista
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

    // Atualiza a lista de sugestões baseado no texto digitado
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

    // Método principal que inicia a aplicação
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
