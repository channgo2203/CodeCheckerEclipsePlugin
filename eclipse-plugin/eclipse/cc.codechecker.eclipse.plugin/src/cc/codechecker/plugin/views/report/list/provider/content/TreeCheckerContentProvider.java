package cc.codechecker.plugin.views.report.list.provider.content;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.joda.time.Instant;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import cc.codechecker.api.action.BugPathItem;
import cc.codechecker.api.action.bug.path.ProblemInfo;
import cc.codechecker.api.action.result.ReportInfo;
import cc.codechecker.api.job.ProblemInfoJob;
import cc.codechecker.api.job.report.list.SearchList;
import cc.codechecker.plugin.config.CodeCheckerContext;
import cc.codechecker.plugin.views.report.list.ReportListView;

public class TreeCheckerContentProvider implements ITreeContentProvider {

    private final ReportListView reportListView;

    public TreeCheckerContentProvider(ReportListView reportListView) {
        this.reportListView = reportListView;
    }

    @Override
    public Object getParent(Object child) {
        if (child instanceof String) {
            String checker = (String) child;
            for (String s : this.reportListView.getReportList().get().getCheckers()) {
                if (checker.startsWith(s + ".")) {
                    return s;
                }
            }
            return this.reportListView.getReportList().orNull();
        }

        if (child instanceof ReportInfo) {
            return ((ReportInfo) child).getCheckerId();
        }

        return null;
    }

    @Override
    public void dispose() {
        // nop
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // nop
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public Object[] getChildren(Object parentElement) {

        if (parentElement instanceof SearchList) {
            Set<String> tops = new HashSet<>();
            for (String s : this.reportListView.getReportList().get().getCheckers()) {
                tops.add(s.split("\\.")[0]);
            }
            return tops.toArray();
        }

        if (parentElement instanceof String) {

            String parent = (String) parentElement;
            int parentDots = parent.split("\\.").length;

            Set<Object> tops = new HashSet<>();
            for (String s : this.reportListView.getReportList().get().getCheckers()) {
                if (s.startsWith(parent) && !s.equals(parent)) {
                    tops.add(parent + "." + s.split("\\.")[parentDots]);
                }
            }

            tops.addAll(this.reportListView.getReportList().get().getReportsFor((String)
                    parentElement));

            return tops.toArray();
        }

        if (parentElement instanceof ReportInfo) {
        	ReportInfo ri = (ReportInfo) parentElement;
        	Optional<ProblemInfo> bp = ri.getChildren();
        	if (bp != null && bp.isPresent()) {
                ArrayList<BugPathItem> result = new ArrayList<>(bp.get().getItems());
                Iterables.removeIf(result, new Predicate<BugPathItem>() {
                    @Override
                    public boolean apply(BugPathItem pi) {
                        return "".equals(pi.getMessage());
                    }
                });
                return result.toArray();
            }
            return ArrayUtils.toArray();
        }
        
        if(parentElement instanceof BugPathItem) {
        	//no child!
        }
 
        return ArrayUtils.toArray();
    }

    @Override
    public boolean hasChildren(Object element) {
    	if(element instanceof ReportInfo || element instanceof SearchList || element instanceof String) {
        	return true;
        }
        return false;
    }

}
