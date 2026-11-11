package com.mycompany.warehouse.system.util;

import javax.swing.Timer;

/**
 * Menunda aksi sampai user berhenti mengetik selama {@code delayMs}.
 * Setiap {@link #trigger()} me-restart timer, jadi aksi hanya jalan sekali
 * setelah ketikan terakhir. Aksi dijalankan di EDT (javax.swing.Timer).
 */
public final class Debouncer {

    private final Timer timer;

    public Debouncer(int delayMs, Runnable action) {
        timer = new Timer(delayMs, e -> action.run());
        timer.setRepeats(false);
    }

    /** Jadwalkan (ulang) aksi. */
    public void trigger() {
        timer.restart();
    }

    /** Batalkan aksi yang masih menunggu. */
    public void cancel() {
        timer.stop();
    }
}
