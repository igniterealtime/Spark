package freeseawind.ninepatch.common;

import java.util.List;

/**
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class NinePatchRegion
{
    private List<Region> fixRegions;
    private List<Region> patchRegions;

    public NinePatchRegion(List<Region> fixRegions, List<Region> patchRegions)
    {
        this.fixRegions = fixRegions;
        this.patchRegions = patchRegions;
    }

    public List<Region> getFixRegions()
    {
        return fixRegions;
    }

    public List<Region> getPatchRegions()
    {
        return patchRegions;
    }

    @Override
    public String toString()
    {
        return "NinePatchRegion [fixRegions=" + fixRegions + ", patchRegions="
                + patchRegions + "]";
    }
}
