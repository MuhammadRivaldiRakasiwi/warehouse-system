package com.mycompany.warehouse.system.util;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 * Menjalankan pekerjaan berat (query database) di background thread lalu
 * memperbarui UI di Event Dispatch Thread (EDT), supaya tampilan tidak freeze.
 *
 * <pre>
 * UiWorker.run(
 *     () -&gt; ambilDataDariDatabase(),        // background thread: JANGAN sentuh komponen Swing
 *     hasil -&gt; tabel.setModel(...),          // EDT: boleh update komponen Swing
 *     error -&gt; JOptionPane.showMessageDialog(...)); // EDT
 * </pre>
 */
public final class UiWorker {

    private static final Logger logger = Logger.getLogger(UiWorker.class.getName());

    private UiWorker() {
    }

    /** Pekerjaan background yang boleh melempar exception (mis. SQLException). */
    @FunctionalInterface
    public interface Task<T> {
        T call() throws Exception;
    }

    /** Callback yang dijalankan di EDT. */
    @FunctionalInterface
    public interface Callback<T> {
        void accept(T value);
    }

    /**
     * @param task      dijalankan di background thread
     * @param onSuccess dijalankan di EDT dengan hasil task (boleh null)
     * @param onError   dijalankan di EDT jika task gagal (boleh null, error hanya dicatat di log)
     */
    public static <T> void run(Task<T> task, Callback<T> onSuccess, Callback<Exception> onError) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return task.call();
            }

            @Override
            protected void done() {
                T result;
                try {
                    result = get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (CancellationException e) {
                    return;
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    Exception error = cause instanceof Exception ex ? ex : new Exception(cause.toString(), cause);
                    if (onError != null) {
                        onError.accept(error);
                    } else {
                        logger.log(Level.SEVERE, "Background task gagal", error);
                    }
                    return;
                }
                if (onSuccess != null) {
                    onSuccess.accept(result);
                }
            }
        }.execute();
    }
}
