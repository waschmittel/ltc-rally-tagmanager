package de.flubba.tagmanager.smartcard;

import lombok.extern.slf4j.Slf4j;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardNotPresentException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
public class ReaderThread extends Thread {
    private final CardAction cardCallback;
    private boolean terminalPresent = false;

    public ReaderThread(CardAction cardCallback) {
        this.cardCallback = cardCallback;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            findTerminalAndReadTags();
            try {
                sleep(3000); // wait while no reader is connected
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }

    private void findTerminalAndReadTags() {
        try {
            while (!isInterrupted()) {
                CardTerminal terminal = getFirstTerminal();
                readTag(terminal);
            }
        } catch (Exception e) {
            log.error("NFC reader error: {}", e.getMessage());
            terminalPresent = false;
        }
    }

    private void readTag(CardTerminal terminal) throws CardException {
        try {
            for (int i = 0; i < 10 && !isInterrupted() && !terminal.isCardPresent(); i++) {
                terminal.waitForCardPresent(500);
            }
            if (terminal.isCardPresent()) {
                try {
                    cardCallback.doWithTagId(readTagId(terminal));
                } catch (Exception e) {
                    log.error("Error processing card: {}", e.getMessage(), e);
                }
            }
            for (int i = 0; i < 10 && !isInterrupted() && terminal.isCardPresent(); i++) {
                terminal.waitForCardAbsent(500);
            }
        } catch (CardNotPresentException e) {
            log.error("Whoops, too fast! Could not read the card: {}", e.getMessage(), e);
        }
    }

    private static String readTagId(CardTerminal terminal) throws CardException {
        Card card = terminal.connect("*"); // connect with any card
        try {
            CardChannel channel = card.getBasicChannel();
            ResponseAPDU response = channel.transmit(new CommandAPDU(new byte[]{(byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00}));
            if (response.getSW1() == 0x63 && response.getSW2() == 0x00) {
                throw new CardException("Got wrong response code");
            }
            return bin2hex(response.getData());
        } finally {
            card.disconnect(false);
        }
    }

    private static List<CardTerminal> getCardTerminals() throws NoSuchAlgorithmException, CardException {
        TerminalFactory factory = TerminalFactory.getInstance("PC/SC", null);
        return factory.terminals().list();
    }

    private CardTerminal getFirstTerminal() throws NoSuchAlgorithmException, CardException, NoTerminalException {
        List<CardTerminal> terminals = getCardTerminals();
        if (terminals.isEmpty()) {
            throw new NoTerminalException();
        }
        CardTerminal terminal = terminals.getFirst();
        if (!terminalPresent) {
            log.info("NFC reader connected: {}", terminal.getName());
            terminalPresent = true;
        }
        return terminal;
    }

    private static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    public static final class NoTerminalException extends Exception {
        private NoTerminalException() {
            super("No NFC Reader found.");
        }
    }
}
