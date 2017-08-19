package EventManagement;

import java.util.List;

/**
 * StubHubAncestors
 * contains detailed information about an event including the
 * category, groupings and performers
 * @author Jordan Gaston
 * @version 0.1.17
 */
public class StubHubAncestors {

    private final List<StubHubCategory> categories;
    private final List<StubHubGrouping> groupings;
    private final List<StubHubPerformer> performers;


    public StubHubAncestors(List<StubHubCategory> categories, List<StubHubGrouping> groupings, List<StubHubPerformer> performers) {
        this.categories = categories;
        this.groupings = groupings;
        this.performers = performers;
    }

    public List<StubHubCategory> getCategories() {
        return categories;
    }

    public List<StubHubGrouping> getGroupings() {
        return groupings;
    }

    public List<StubHubPerformer> getPerformers() {
        return performers;
    }
}
