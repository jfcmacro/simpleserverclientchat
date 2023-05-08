package com.epam.rd.chat.client;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.io.IOException;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;

class ChatWindow extends BasicWindow
    implements ChatObserver {
    private Terminal terminal;
    private Screen screen;
    private BasicWindow window;
    private Panel mainPanel;
    private Panel remotePanel;
    private Panel localPanel;
    private Panel buttonsPanel;
    private Panel statusPanel;
    private Label remoteLabels[];
    private Label localLabels[];
    private Label statusLabels[];
    private TextBox inputText;
    private Button buttonSend;
    private Button buttonCancel;
    private Button buttonClose;
    private MultiWindowTextGUI gui;
    private Map<ChatEvent, BiConsumer<ChatEvent,String>> eventActions;
    private ChatClientControl chatClientCtrl;
    private IChatClient chatClient;

    private static void pushMsgAtLabelArray(Label[] labels, String newMsg) {
        int i = 1;
        for (; i < labels.length; i++)
            labels[i-1].setText(labels[i].getText());
        labels[i-1].setText(newMsg);
    }

    private void pushRemoteMsg(String newMsg) {
        pushMsgAtLabelArray(remoteLabels, newMsg);
    }

    private void pushLocalMsg(String newMsg) {
        pushMsgAtLabelArray(localLabels, newMsg);
    }

    ChatWindow(IChatClient chatClient, ChatClientControl chatClientCtrl)
        throws IOException {
        this.chatClient = chatClient;
        ((ObservableChatEvent) this.chatClient).addObserver(this);
        this.chatClientCtrl = chatClientCtrl;
        terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        eventActions = new HashMap<>();
        screen.startScreen();
        this.setHints(Arrays.asList(Window.Hint.FULL_SCREEN,
                                    Window.Hint.NO_DECORATIONS));

        TerminalSize termSize = terminal
            .getTerminalSize();

        int panelCols = termSize.getColumns();
        int panelRows = (termSize.getRows() - 1) / 2;

        termSize = termSize
            .withRows(termSize.getRows())
            .withColumns(termSize.getColumns());

        mainPanel = new Panel()
            .setPreferredSize(termSize)
            .setLayoutManager(new LinearLayout(Direction.VERTICAL));

        remotePanel = new Panel()
            .setPreferredSize(new TerminalSize(panelCols,
                                               panelRows))
            .setLayoutManager(new LinearLayout(Direction.VERTICAL));

        remoteLabels = new Label[panelRows - 2];
        for (int i = 0; i < panelRows - 2; i++) {
            remoteLabels[i] = new Label("");
            remotePanel.addComponent(remoteLabels[i]);
        }

        localPanel = new Panel()
            .setPreferredSize(new TerminalSize(panelCols,
                                               panelRows));

        localLabels = new Label[panelRows - 4];
        for (int i = 0; i < panelRows - 4; i++) {
            localLabels[i] = new Label("");
            localPanel.addComponent(localLabels[i]);
        }

        inputText = new TextBox(new TerminalSize(panelCols - 3, 1));

        inputText
            .handleKeyStroke(new KeyStroke(KeyType.Enter));

        localPanel.addComponent(inputText);

        buttonsPanel = new Panel()
            .setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        buttonSend = new Button("Send");

        buttonSend.addListener(button -> {
                try {
                    this.chatClientCtrl.sendMessage(inputText.getText());
                }
                catch(IOException ioe) {
                    try {
                        screen.stopScreen();
                        this.chatClientCtrl.disconnect();
                    } catch (IOException ioe2) { }
                    System.out.println("IOException: " + ioe);
                }
                inputText.setText("");
                inputText.takeFocus();
            });

        buttonCancel = new Button("Cancel");

        buttonCancel.addListener(button -> inputText
                                 .setText("").takeFocus());

        buttonClose = new Button("Close");

        buttonClose.addListener(button -> {
                if (ChatClientControl.logger != null) {
                    ChatClientControl.logger.info("Close");
                }
                try {
                    screen.stopScreen();
                    this.chatClientCtrl.disconnect();
                } catch (IOException ioe2) { }
            });

        buttonsPanel
            .addComponent(buttonSend)
            .addComponent(buttonCancel)
            .addComponent(buttonClose);

        localPanel.addComponent(buttonsPanel);

        statusPanel = new Panel()
            .setPreferredSize(new TerminalSize(panelCols,
                                               1))
            .setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        statusLabels = new Label[3];
        statusLabels[0] = new Label("Local: status")
            .setLayoutData(LinearLayout
                           .createLayoutData(LinearLayout
                                             .Alignment.Beginning));
        statusLabels[1] = new Label("")
            .setLayoutData(LinearLayout
                           .createLayoutData(LinearLayout
                                             .Alignment.Fill));
        statusLabels[2] = new Label("Remote: status")
            .setLayoutData(LinearLayout
                           .createLayoutData(LinearLayout
                                             .Alignment.End));

        statusPanel
            .addComponent(statusLabels[0])
            .addComponent(statusLabels[1])
            .addComponent(statusLabels[2]);

        mainPanel
            .addComponent(remotePanel
                          .withBorder(Borders.singleLine("Remote")))
            .addComponent(localPanel
                          .withBorder(Borders.singleLine("Local")))
            .addComponent(statusPanel);

        this.setComponent(mainPanel);
        gui = new MultiWindowTextGUI(screen,
                                     new DefaultWindowManager(),
                                     new EmptySpace(TextColor.ANSI.BLUE));

        eventActions.put(ChatEvent.CONNECT_CHATEVENT, (e,d) -> {
                return;
            });
        eventActions.put(ChatEvent.DISCONNECT_CHATEVENT, (e,d) -> {
                return;
            });
        eventActions.put(ChatEvent.RECEIVEMSG_CHATEVENT, (e,d) -> {
                pushRemoteMsg(d);
                return;
            });
        eventActions.put(ChatEvent.SENDMSG_CHATEVENT, (e,d) -> {
                pushLocalMsg(d);
                return;
           });
        gui.addWindowAndWait(this);
    }

    public void handleEvent(ChatEvent event, String data) {
        eventActions.get(event).accept(event, data);
    }
}
