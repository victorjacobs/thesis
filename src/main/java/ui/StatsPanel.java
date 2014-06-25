package ui;

import com.google.common.base.Optional;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import rinde.sim.core.Simulator;
import rinde.sim.core.TickListener;
import rinde.sim.core.TimeLapse;
import rinde.sim.ui.renderers.PanelRenderer;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class StatsPanel implements PanelRenderer, TickListener {
    private static final int PREFERRED_SIZE = 300;
    private Optional<Table> statsTable;
    private Simulator sim;


    public StatsPanel(Simulator sim) {
        this.sim = sim;
        statsTable = Optional.absent();
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
        final String[] statsTitles = new String[] { "Variable", "Value" };
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
        return "StatsPanel";
    }

    @Override
    public void tick(TimeLapse timeLapse) {

    }

    @Override
    public void afterTick(TimeLapse timeLapse) {

    }
}
