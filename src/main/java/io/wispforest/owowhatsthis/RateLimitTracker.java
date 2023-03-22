package io.wispforest.owowhatsthis;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RateLimitTracker {

    private long lastPassTime = -1;
    private int override = -1;

    public void setOverride(int override) {
        this.override = override;
    }

    public void clearOverride() {
        this.override = -1;
    }

    public boolean update(long time) {
        int rateLimit = this.override < 0
                ? OwoWhatsThis.CONFIG.updateDelay()
                : this.override;

        if (time - lastPassTime >= rateLimit) {
            this.lastPassTime = time;
            return true;
        } else {
            return false;
        }
    }

    public void reset() {
        this.lastPassTime = -1;
        this.override = -1;
    }

}
