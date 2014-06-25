package ui;

import com.google.common.base.Optional;
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
import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.ui.renderers.PanelRenderer;

import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;

final class ReAuctionPanel implements PanelRenderer, TickListener {
    private static final int PREFERRED_SIZE = 300;

    Optional<Table> statsTable;
    private Map<String,ReAuctionableParcel> trackedParcels;
    private Optional<PDPModel> pdpModel;

    public ReAuctionPanel(Simulator sim) {
        pdpModel = Optional.absent();
        trackedParcels = newHashMap();

        for (Model<?> mod : sim.getModels()) {
            if (mod instanceof PDPModel) {
                pdpModel = Optional.of((PDPModel) mod);
                break;
            }
        }

        checkState(pdpModel.isPresent(), "There should be a pdpmodel here");
        ReAuctionableParcel casted;

        for (Parcel p : pdpModel.get().getParcels(PDPModel.ParcelState.values())) {
            try {
                casted = (ReAuctionableParcel) p;
                trackedParcels.put(casted.toString(), casted);
            } catch (ClassCastException e) {
                throw new IllegalStateException("This StatsPanel only works with ReAuctionableParcels");
            }
        }

        statsTable = Optional.absent();

        /*sim.getEventAPI().addListener(new Listener() {
            @Override
            public void handleEvent(Event e) {
                PDPModelEvent ev = (PDPModelEvent) e;
                try {
                    ReAuctionableParcel casted = (ReAuctionableParcel) ev.parcel;

                    trackedParcels.put(casted.toString(), casted);
                    final TableItem ti = new TableItem(statsTable.get(), 0);
                    ti.setText(0, casted.toString());
                    ti.setText(1, Integer.toString(casted.getNumberReAuctions()));
                } catch (ClassCastException ex) {
                    System.err.println("StatsPanel only useable with ReAuctionableParcels");
                }
            }
        }, PDPModel.PDPModelEventType.NEW_PARCEL);*/
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
        final String[] statsTitles = new String[] { "Parcel", "ReAuctions" };
        for (String statsTitle : statsTitles) {
            final TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(statsTitle);
        }

        for (int i = 0; i < statsTitles.length; i++) {
            table.getColumn(i).pack();
        }
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
        return "Statistics";
    }

    @Override
    public void tick(TimeLapse timeLapse) {

    }

    @Override
    public void afterTick(TimeLapse timeLapse) {
        if (statsTable.get().isDisposed()
                || statsTable.get().getDisplay().isDisposed()) {
            return;
        }


        statsTable.get().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                if (statsTable.get().isDisposed()) {
                    return;
                }

                // First update existing
                for (int i = 0; i < trackedParcels.size(); i++) {
                    TableItem it = statsTable.get().getItem(i);
                    it.setText(1, Integer.toString(trackedParcels.get(it.getText(0)).getNumberReAuctions()));
                }

                // Add diff
                for (Parcel par : pdpModel.get().getParcels(PDPModel.ParcelState.values())) {
                    try {
                        ReAuctionableParcel casted = (ReAuctionableParcel) par;

                        if (!trackedParcels.values().contains(casted)) {
                            trackedParcels.put(casted.toString(), casted);
                            final TableItem ti = new TableItem(statsTable.get(), 0);
                            ti.setText(0, par.toString());
                            ti.setText(1, Integer.toString(casted.getNumberReAuctions()));
                        }
                    } catch (ClassCastException e) {
                        System.err.println("StatsPanel only useable with ReAuctionableParcels");
                    }
                }
            }
        });
    }
}
