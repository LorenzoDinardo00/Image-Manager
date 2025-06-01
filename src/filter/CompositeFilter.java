package filter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta un filtro composito, ovvero il risultato della composizione
 * di più filtri. Il metodo apply applica in sequenza i filtri aggiunti.
 */
public class CompositeFilter implements Filter {
    private List<Filter> filters = new ArrayList<>();


    public void addFilter(Filter filter) {
        filters.add(filter);
    }


    public void removeFilter(Filter filter) {
        filters.remove(filter);
    }

    @Override
    public BufferedImage apply(BufferedImage inputImage) {
        BufferedImage result = inputImage;
        for (Filter filter : filters) {
            result = filter.apply(result);
        }
        return result;
    }

    public List<Filter> getFilters() {
        return filters;
    }
}

