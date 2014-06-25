package ui;

import com.google.common.base.Optional;
import common.results.ParcelTrackerModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.core.Simulator;
import rinde.sim.core.TickListener;
import rinde.sim.core.TimeLapse;
import rinde.sim.core.model.Model;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.ui.renderers.PanelRenderer;

import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;

class ReAuctionPanel implements PanelRenderer, TickListener {
    private static final int PREFERRED_SIZE = 300;

    private Optional<Table> statsTable;
    private Map<String,ReAuctionableParcel> trackedParcels;
    private Optional<ParcelTrackerModel> parcelTracker;

    public ReAuctionPanel(Simulator sim) {
        parcelTracker = Optional.absent();
        trackedParcels = newHashMap();

        for (Model<?> mod : sim.getModels()) {
            if (mod instanceof ParcelTrackerModel) {
                parcelTracker = Optional.of((ParcelTrackerModel) mod);
                break;
            }
        }

        checkState(parcelTracker.isPresent(), "There should be a pdpmodel here");

        ReAuctionableParcel casted;
        for (Parcel p : parcelTracker.get().getParcels()) {
            try {
                casted = (ReAuctionableParcel) p;
                trackedParcels.put(casted.toString(), casted);
            } catch (ClassCastException e) {
                throw new IllegalStateException("This StatsPanel only works with ReAuctionableParcels");
            }
        }

        statsTable = Optional.absent();
    }

    @Override
    public void initializePanel(Composite parent) {
        final FillLayout layout = new FillLayout();
        layout.marginWidth = 2;
        layout.marginHeight = 2;
        layout.type = SWT.VERTICAL;
        parent.setLayout(layout);

        final Table table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
        statsTable = Optional.of(table);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        final String[] statsTitles = new String[] { "Parcel                  ", "ReAuctions", "Useful % " };
        for (String statsTitle : statsTitles) {
            final TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(statsTitle);
        }

        for (int i = 0; i < statsTitles.length; i++) {
            table.getColumn(i).pack();
        }

        statsTable.get().setSortColumn(statsTable.get().getColumn(1));
        statsTable.get().setSortDirection(1);
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
        return "ReAuctions";
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

                // Don't sort when nothing changed
                boolean changed = false;

                // First update existing
                for (int i = 0; i < trackedParcels.size(); i++) {
                    TableItem it = statsTable.get().getItem(i);
                    ReAuctionableParcel par = trackedParcels.get(it.getText(0));
                    int newReAuctionValue = par.getNumberReAuctions();
                    float newPercentage =  par.getPercentageUsefulReAuctions();

                    if (Integer.parseInt(it.getText(1)) != newReAuctionValue) {
                        it.setText(1, Integer.toString(newReAuctionValue));
                        changed = true;
                    }

                    it.setText(2, Integer.toString(Math.round(newPercentage * 100)));
                }

                // Add new parcels (this works because both are *lists*
                while (trackedParcels.size() != parcelTracker.get().getParcels().size()) {
                    try {
                        // Get parcel
                        ReAuctionableParcel par = parcelTracker.get().getParcels().get(trackedParcels.size());
                        trackedParcels.put(Integer.toString(par.hashCode()), par);

                        final TableItem ti = new TableItem(statsTable.get(), 0);
                        ti.setText(0, Integer.toString(par.hashCode()));
                        ti.setText(1, Integer.toString(par.getNumberReAuctions()));
                        ti.setText(2, Integer.toString(Math.round(par.getPercentageUsefulReAuctions() * 100)));
                        changed = true;
                    } catch (ClassCastException e) {
                        System.err.println("ReAuctionPanel only useable with ReAuctionableParcels");
                    }
                }

                // Sort
                if (changed) {
                    TableItem[] items = statsTable.get().getItems();
                    int index = 1;
                    for (int i = 1; i < items.length; i++) {
                        String value1 = items[i].getText(index);
                        for (int j = 0; j < i; j++){
                            String value2 = items[j].getText(index);
                            if (Integer.parseInt(value1) > Integer.parseInt(value2)) {
                                String[] values = {items[i].getText(0), items[i].getText(1)};
                                items[i].dispose();
                                TableItem item = new TableItem(statsTable.get(), SWT.NONE, j);
                                item.setText(values);
                                items = statsTable.get().getItems();
                                break;
                            }
                        }
                    }
                }
            }
        });
    }
}
