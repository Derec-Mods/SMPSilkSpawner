package xmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("xmod")
public class XMod
{
    public XMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
