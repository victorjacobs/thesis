package ui;

import com.google.common.base.Optional;
import common.results.ParcelTrackerModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import rinde.sim.core.Simulator;
import rinde.sim.core.TickListener;
import rinde.sim.core.TimeLapse;
import rinde.sim.core.model.Model;
import rinde.sim.ui.renderers.PanelRenderer;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class StatsPanel implements PanelRenderer, TickListener {
    private static final int PREFERRED_SIZE = 300;
    private Optional<Table> statsTable;
    private final Simulator sim;
    private Optional<ParcelTrackerModel> parcels;


    public StatsPanel(Simulator sim) {
        this.parcels = Optional.absent();
        this.sim = sim;
        this.statsTable = Optional.absent();

        for (Model<?> m : sim.getModels()) {
            if (m instanceof ParcelTrackerModel) {
                parcels = Optional.of((ParcelTrackerModel) m);
                break;
            }
        }
    }

    @Override
    public void initializePanel(Composite parent) {
        final FillLayout layout = new FillLayout();
        layout.marginWidth = 2;
        layout.marginHeight = 2;
        layout.type = SWT.VERTICAL;
        parent.setLayout(layout);

        final Table table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION
                | SWT.V_SCROLL | SWT.H_SCROLL);
        statsTable = Optional.of(table);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        final String[] statsTitles = new String[] { "Variable                             ", "Value    " };
        for (String statsTitle : statsTitles) {
            final TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(statsTitle);
        }

        for (int i = 0; i < statsTitles.length; i++) {
            table.getColumn(i).pack();
        }

        /*final TableItem t1 = new TableItem(statsTable.get(), 0);
        t1.setText(0, "Overall efficiency");
        t1.setText(1, "N/A");*/

        final TableItem t2 = new TableItem(statsTable.get(), 1);
        t2.setText(0, "Auctions per parcel");
        t2.setText(1, "N/A");
    }

    @Override
    public int preferredSize() {
        return PREFERRED_SIZE;
    }

    @Override
    public int getPreferredPosition() {
        return SWT.LEFT;
    }

    @Override
    public String getName() {
        return "Stats";
    }

    @Override
    public void tick(TimeLapse timeLapse) {

    }

    @Override
    public void afterTick(TimeLapse timeLapse) {
        if (statsTable.get().isDisposed() || statsTable.get().getDisplay().isDisposed()) {
            return;
        }

        statsTable.get().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                if (statsTable.get().isDisposed()) {
                    return;
                }

                /*statsTable.get().getItem(0).setText(1,
                        Integer.toString(Math.round(parcels.get().getOverallEfficiency() * 1000) / 10) + "%");*/
                if (parcels.get().getParcels().size() != 0)
                    statsTable.get().getItem(0).setText(1, Float.toString(parcels.get().getReAuctionsPerParcel()));
            }
        });
    }
}
