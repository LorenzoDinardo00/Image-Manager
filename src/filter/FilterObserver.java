package filter;

/**
 * Interfaccia per gli osservatori interessati ad aggiornamenti
 * relativi all'aggiunta di nuovi filtri.
 */
public interface FilterObserver {

    void onFilterAdded(Filter filter);
}
