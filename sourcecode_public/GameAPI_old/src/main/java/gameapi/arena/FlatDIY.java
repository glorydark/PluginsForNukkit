//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package gameapi.arena;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Glorydark
 */
public class FlatDIY extends Generator {
    private ChunkManager level;
    private NukkitRandom random;
    private final List<Populator> populators;
    private int[][] structure;
    private final Map<String, Object> options;
    private int floorLevel;
    private String preset;
    private boolean init;
    private int biome;

    public int getId() {
        return 5;
    }

    public ChunkManager getChunkManager() {
        return this.level;
    }

    public Map<String, Object> getSettings() {
        return this.options;
    }

    public String getName() {
        return "DIY";
    }

    public FlatDIY() {
        this(new HashMap());
    }

    public FlatDIY(Map<String, Object> options) {
        this.populators = new ArrayList();
        this.init = false;
        String string = "2;7";
        int height = 64;
        for(int i=0;i<=height;i++){
            string += ",56";
        }

        this.preset = string+";"; //"5;7,2x56,2;1;" 草方块(2)1基岩(7)1
        // 56x3 指的是 56层泥土 基岩7->泥土3(2层)->顶层
        this.options = options;
    }

    protected void parsePreset(String preset, int chunkX, int chunkZ) {
        try {
            this.preset = preset;
            String[] presetArray = preset.split(";"); //分割逗号也就是 5、7,2x3,2、1
            int version = Integer.parseInt(presetArray[0]); //version = 5
            String blocks = presetArray.length > 1 ? presetArray[1] : ""; //7
            this.biome = presetArray.length > 2 ? Integer.parseInt(presetArray[2]) : 1;
            String options = presetArray.length > 3 ? presetArray[1] : "";
            this.structure = new int[256][];
            int y = 0;
            String[] var9 = blocks.split(",");
            int var10 = var9.length;

            int var11;
            String option;
            String[] s;
            for(var11 = 0; var11 < var10; ++var11) {
                option = var9[var11];
                int meta = 0;
                int cnt = 1;
                int id;
                if (Pattern.matches("^[0-9]{1,3}x[0-9]$", option)) {
                    s = option.split("x");
                    cnt = Integer.parseInt(s[0]);
                    id = Integer.parseInt(s[1]);
                } else if (Pattern.matches("^[0-9]{1,3}:[0-9]{0,2}$", option)) {
                    s = option.split(":");
                    id = Integer.parseInt(s[0]);
                    meta = Integer.parseInt(s[1]);
                } else {
                    if (!Pattern.matches("^[0-9]{1,3}$", option)) {
                        continue;
                    }

                    id = Integer.parseInt(option);
                }

                int cY = y;
                y += cnt;
                if (y > 255) {
                    y = 255;
                }

                while(cY < y) {
                    this.structure[cY] = new int[]{id, meta};
                    ++cY;
                }
            }

            for(this.floorLevel = y; y <= 255; ++y) {
                this.structure[y] = new int[]{0, 0};
            }

            var9 = options.split(",");
            var10 = var9.length;

            for(var11 = 0; var11 < var10; ++var11) {
                option = var9[var11];
                if (Pattern.matches("^[0-9a-z_]+$", option)) {
                    this.options.put(option, true);
                } else if (Pattern.matches("^[0-9a-z_]+\\([0-9a-z_ =]+\\)$", option)) {
                    String name = option.substring(0, option.indexOf("("));
                    String extra = option.substring(option.indexOf("(") + 1, option.indexOf(")"));
                    Map<String, Float> map = new HashMap();
                    s = extra.split(" ");
                    int var17 = s.length;

                    for(int var18 = 0; var18 < var17; ++var18) {
                        String kv = s[var18];
                        String[] data = kv.split("=");
                        map.put(data[0], Float.valueOf(data[1]));
                    }

                    this.options.put(name, map);
                }
            }

        } catch (Exception var21) {
            Server.getInstance().getLogger().error("error while parsing the preset", var21);
            throw new RuntimeException(var21);
        }
    }

    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
        this.random = random;
    }

    public void generateChunk(int chunkX, int chunkZ) {
        if (!this.init) {
            this.init = true;
            if (this.options.containsKey("preset") && !"".equals(this.options.get("preset"))) {
                this.parsePreset((String)this.options.get("preset"), chunkX, chunkZ);
            } else {
                this.parsePreset(this.preset, chunkX, chunkZ);
            }
        }

        this.generateChunk(this.level.getChunk(chunkX, chunkZ));
    }

    private void generateChunk(FullChunk chunk) {
        chunk.setGenerated();

        for(int Z = 0; Z < 16; ++Z) {
            for(int X = 0; X < 16; ++X) {
                chunk.setBiomeId(X, Z, this.biome);

                for(int y = 0; y < 256; ++y) {
                    int k = this.structure[y][0];
                    int l = this.structure[y][1];
                    chunk.setBlock(X, y, Z, this.structure[y][0], this.structure[y][1]);
                }
            }
        }

    }

    public void populateChunk(int chunkX, int chunkZ) {
        BaseFullChunk chunk = this.level.getChunk(chunkX, chunkZ);
        this.random.setSeed((long)(-559038737 ^ chunkX << 8 ^ chunkZ) ^ this.level.getSeed());
        Iterator var4 = this.populators.iterator();

        while(var4.hasNext()) {
            Populator populator = (Populator)var4.next();
            populator.populate(this.level, chunkX, chunkZ, this.random, chunk);
        }

    }

    public Vector3 getSpawn() {
        return new Vector3(128.0D, (double)this.floorLevel, 128.0D);
    }
}
