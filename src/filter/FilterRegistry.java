package filter;

import java.util.ArrayList;
import java.util.List;

public class FilterRegistry {
    private final List<Filter> filters = new ArrayList<>();
    public FilterRegistry() {
        addFilter(new GrayScaleFilter());
        addFilter(new InvertFilter());
        addFilter(new BlurFilter());
        addFilter(new SharpenFilter());
        addFilter(new SepiaFilter());

    }


    public void addFilter(Filter filter) {
        if (filter != null && !filters.contains(filter)) {
            filters.add(filter);
        }
    }

    public List<Filter> getAvailableFilters() {
        return new ArrayList<>(filters);
    }
}