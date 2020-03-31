package net.dashflight.data.keys

import java.math.BigInteger
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec

/**
 * Always returns the same hardcoded RSA keys. Used for testing.
 */
class StaticRSAKeyPairProvider : RSAKeyPairProvider {
    override lateinit var publicKey: RSAPublicKey
    override lateinit var privateKey: RSAPrivateKey

    init {
        try {
            val keyFactory = KeyFactory.getInstance("RSA")
            val privateKeySpec = RSAPrivateKeySpec(
                    BigInteger(
                            "125575066303978004779628661271936757461237441669399546642565475269653434593882700068970936181460382739964728687769648151411401937227232598985607099915671042378689782162280498696932783906357951506170881138350175901318015049127406882183230492243314190582966653112575255015661039726865251840287297902147291939807"),
                    BigInteger(
                            "6752315999438767243593869147539636133686325960037291947577715410382817393363178586799114684887413045693817292456173460573016470494358418585307222181406299865757895452076870062679568840073919411473702909145418811267046266521518978542328469971022288821426004784913579087437120510492344261075286343542842857985")
            )
            val publicKeySpec = RSAPublicKeySpec(
                    BigInteger(
                            "125575066303978004779628661271936757461237441669399546642565475269653434593882700068970936181460382739964728687769648151411401937227232598985607099915671042378689782162280498696932783906357951506170881138350175901318015049127406882183230492243314190582966653112575255015661039726865251840287297902147291939807"),
                    BigInteger("65537")
            )
            publicKey = keyFactory.generatePublic(publicKeySpec) as RSAPublicKey
            privateKey = keyFactory.generatePrivate(privateKeySpec) as RSAPrivateKey
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}