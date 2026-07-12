package hjsonpp.expand.entities.bullets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;

import static mindustry.Vars.headless;

public class TrueArtilleryBulletType extends BasicBulletType {

    public float trailMult = 1f, trailSize = 4f;

    private static float cdist = 0f;
    private static Unit result;

    //projectile trajectory height
    public float trajectoryZ = 50;

    public float angleFactor = 0.25f;

    private float bulletZ;

    public TrueArtilleryBulletType() {
        super(1, 1, "shell");
        collidesTiles = false;
        collides = false;
        collidesAir = false;
        scaleLife = true;
        hitShake = 1f;
        hitSound = Sounds.explosionArtillery;
        hitEffect = Fx.flakExplosion;
        shootEffect = Fx.shootBig;
        trailEffect = Fx.artilleryTrail;
        //default settings:
        shrinkX = 0.15f;
        shrinkY = 0.5f;
        shrinkInterp = Interp.slope;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        drawSize += trajectoryZ * 1.2f;
    }

    @Override
    public void updateTrail(Bullet b){
        if(!headless && trailLength > 0){
            if(b.trail == null){
                b.trail = new Trail(trailLength);
            }
            b.trail.length = trailLength;
            b.trail.update(b.x, b.y + bulletZ, trailInterp.apply(b.fin()) * (1f + (trailSinMag > 0 ? Mathf.absin(Time.time, trailSinScl, trailSinMag) : 0f)));
        }
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        if(b.time >= b.lifetime -2){
            cdist = 0f;
            result = null;
            float range = b.hitSize;

            Units.nearbyEnemies(b.team, b.x - range, b.y - range, range * 2f, range * 2f, e -> {
                if (e.dead() || !e.checkTarget(collidesAir, collidesGround) || !e.hittable()) return;

                e.hitbox(Tmp.r1);
                if (!Tmp.r1.contains(b.x, b.y)) return;

                float dst = e.dst(b.x, b.y) - e.hitSize;
                if ((result == null || dst < cdist)) {
                    result = e;
                    cdist = dst;
                }
            });

            if (result != null) {
                b.collision(result, b.x, b.y);
            } else if (collidesTiles) {
                Building build = Vars.world.buildWorld(b.x, b.y);
                if (build != null && build.team != b.team) {
                    build.collision(b);
                    hit(b, b.x, b.y);
                    b.hit = true;
                }
            }
        }
    }

    @Override
    public void draw(Bullet b){
        float zLayer = Draw.z();
        if(trailLength > 0 && b.trail != null){
            //draw below bullets
            Draw.z(zLayer - 0.0001f);
            b.trail.draw(trailColor, trailWidth);
            Draw.z(zLayer);
        }

        float offset = -90 + (spin != 0 ? Mathf.randomSeed(b.id, 360f) + b.time * spin : 0f) + rotationOffset;
        float pr = Mathf.sinDeg(b.time() / b.lifetime * 180);

        float shrink = shrinkInterp.apply(b.fout());
        float height = this.height * ((1f - shrinkY) + shrinkY * shrink);
        float width = this.width * ((1f - shrinkX) + shrinkX * shrink);

        float cPr = Mathf.sinDeg(b.time() / b.lifetime * 180 + 90);




        bulletZ =  Mathf.sinDeg(b.rotation()) * pr * trajectoryZ * (b.lifetime / b.type.lifetime);

        if(b.timer(0, (3 + b.fslope() * 2f) * trailMult)){
            trailEffect.at(b.x, b.y + bulletZ, trailRotation ? b.rotation() : b.fslope() * trailSize, backColor);
        }

        Draw.z(Layer.darkness);

        Draw.color(Pal.shadow, Pal.shadow.a);

        Draw.rect(frontRegion, b.x , b.y, Mathf.lerp(width, width + trajectoryZ / 8, pr * (b.lifetime / b.type.lifetime)), Mathf.lerp(height, height + trajectoryZ / 8, pr * (b.lifetime / b.type.lifetime)), b.rotation() + offset);

        Draw.z(zLayer);

        Color mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin());
        Draw.mixcol(mix, mix.a);

        float angle = b.rotation() > 180 ? 270 : 90;

        float str = Math.abs(Mathf.sinDeg(b.rotation()));

        float drawRotation = Mathf.lerp(b.rotation(), angle, cPr * angleFactor * str);

        if(backRegion.found()){
            Draw.color(backColor);
            Draw.rect(backRegion, b.x, b.y + bulletZ, width, height, drawRotation + offset);
        }

        Draw.color(frontColor);
        Draw.rect(frontRegion, b.x, b.y + bulletZ, width, height, drawRotation + offset);
        Draw.reset();
    }
}
