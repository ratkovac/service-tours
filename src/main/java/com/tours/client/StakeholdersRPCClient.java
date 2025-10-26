package com.tours.client;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RPC Client za komunikaciju sa Stakeholders servisom
 * 
 * PRIMER RPC:
 * 
 * 1. Tours servis šalje poruku: "Proveri da li korisnik X postoji"
 * 2. Stakeholders servis prima poruku, obrađuje je
 * 3. Stakeholders servis vraća odgovor: "Da, korisnik postoji" ili "Ne, ne postoji"
 * 
 * Ovo je RPC jer izgleda kao lokalni metod poziv:
 * boolean exists = stakeholdersClient.checkUserExists("username");
 */
@Service
public class StakeholdersRPCClient {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public StakeholdersRPCClient(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Proverava da li korisnik postoji u Stakeholders servisu
     * 
     * Ovo je RPC poziv - izgleda kao lokalna metoda, ali radi na drugom serveru!
     * 
     * @param username - korisničko ime
     * @return true ako korisnik postoji, false ako ne
     */
    public boolean checkUserExists(String username) {
        try {
            System.out.println("🔄 RPC: Proveravam da li korisnik '" + username + "' postoji...");
            
            // Salje poruku sa routing key "check.user.exists"
            Object response = rabbitTemplate.convertSendAndReceive(
                "stakeholders-rpc-exchange",  // Exchange u Stakeholders servisu
                "check.user.exists",           // Routing key
                username                       // Poruka = username
            );

            boolean exists = response != null && Boolean.parseBoolean(response.toString());
            System.out.println("✅ RPC odgovor: Korisnik postoji = " + exists);
            return exists;
            
        } catch (Exception e) {
            System.err.println("❌ RPC greška pri proveri korisnika: " + e.getMessage());
            // Fallback: ako RPC ne radi, vrati true (ne blokiraj)
            return true;
        }
    }

    /**
     * Proverava da li je korisnik blokiran
     * 
     * RPC poziv koji vraća true/false
     * 
     * @param username - korisničko ime
     * @return true ako je blokiran, false ako nije
     */
    public boolean isUserBlocked(String username) {
        try {
            System.out.println("🔄 RPC: Proveravam da li je korisnik '" + username + "' blokiran...");
            
            Object response = rabbitTemplate.convertSendAndReceive(
                "stakeholders-rpc-exchange",
                "check.user.blocked",
                username
            );

            boolean blocked = response != null && Boolean.parseBoolean(response.toString());
            System.out.println("✅ RPC odgovor: Korisnik blokiran = " + blocked);
            return blocked;
            
        } catch (Exception e) {
            System.err.println("❌ RPC greška pri proveri blokiranog korisnika: " + e.getMessage());
            return false;
        }
    }

    /**
     * Dobija korisničku ulogu (ROLE_TOURIST, ROLE_GUIDE, itd.)
     * 
     * RPC poziv koji vraća ulogu korisnika
     * 
     * @param username - korisničko ime
     * @return uloga korisnika ili null
     */
    public String getUserRole(String username) {
        try {
            System.out.println("🔄 RPC: Dobijam ulogu korisnika '" + username + "'...");
            
            Object response = rabbitTemplate.convertSendAndReceive(
                "stakeholders-rpc-exchange",
                "get.user.role",
                username
            );

            String role = response != null ? response.toString() : null;
            System.out.println("✅ RPC odgovor: Uloga = " + role);
            return role;
            
        } catch (Exception e) {
            System.err.println("❌ RPC greška pri dobijanju uloge: " + e.getMessage());
            return null;
        }
    }
}

