package ui;

import org.eclipse.swt.graphics.GC;
import rinde.sim.ui.renderers.CanvasRenderer;
import rinde.sim.ui.renderers.ViewPort;
import rinde.sim.ui.renderers.ViewRect;

import javax.annotation.Nullable;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ReAuctionPingRenderer implements CanvasRenderer {
    @Override
    public void renderStatic(GC gc, ViewPort vp) {

    }

    @Override
    public void renderDynamic(GC gc, ViewPort vp, long time) {

    }

    @Nullable
    @Override
    public ViewRect getViewRect() {
        return null;
    }
}
