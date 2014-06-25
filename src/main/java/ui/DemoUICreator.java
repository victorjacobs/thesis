package ui;

import org.eclipse.swt.SWT;
import rinde.sim.core.Simulator;
import rinde.sim.core.model.pdp.Depot;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.core.model.pdp.Vehicle;
import rinde.sim.pdptw.common.RouteRenderer;
import rinde.sim.scenario.ScenarioController;
import rinde.sim.ui.View;
import rinde.sim.ui.renderers.PDPModelRenderer;
import rinde.sim.ui.renderers.PlaneRoadModelRenderer;
import rinde.sim.ui.renderers.RoadUserRenderer;
import rinde.sim.ui.renderers.UiSchema;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class DemoUICreator implements ScenarioController.UICreator {
    private String title;

    public DemoUICreator(String title) {
        this.title = title;
    }

    @Override
    public void createUI(Simulator sim) {
        final UiSchema roadUserSchema = new UiSchema(false);
        roadUserSchema.add(Vehicle.class, SWT.COLOR_RED);
        roadUserSchema.add(Depot.class, SWT.COLOR_CYAN);
        roadUserSchema.add(Parcel.class, SWT.COLOR_BLUE);

        View.Builder b = View.create(sim);
        b.setTitleAppendix(title)
                .with(new RouteRenderer())
                .with(new RoadUserRenderer(roadUserSchema, false))
                .with(new PDPModelRenderer(false))
                .with(new PlaneRoadModelRenderer(0.05))
                .with(new ReAuctionPanel(sim))
                .with(new StatsPanel(sim))
                .setSpeedUp(5)
                .enableAutoPlay()
                .show();
    }
}
