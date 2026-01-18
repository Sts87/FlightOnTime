package com.flightontime.api.validation;

import java.util.Set;

/**
 * Constantes utilizadas para validaciones en la aplicación.
 * Contiene listas de códigos válidos y patrones de validación.
 */
public final class ValidationConstants {

    private ValidationConstants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no debe ser instanciada");
    }

    /**
     * Códigos IATA válidos de aerolíneas.
     * Usando Set para búsquedas más eficientes O(1).
     */
    public static final Set<String> VALID_AIRLINE_CODES = Set.of(
            "CO", "US", "AA", "AS", "DL", "B6", "HA", "OO",
            "9E", "OH", "EV", "XE", "YV", "UA", "MQ", "FL", "F9", "WN"
    );

    /**
     * Códigos IATA válidos de aeropuertos (3 caracteres).
     * Esta lista contiene todos los aeropuertos del archivo airport_mappings.json
     * más aeropuertos adicionales que se pueden agregar en el futuro.
     *
     * NOTA: Si un aeropuerto está en esta lista pero no en airport_mappings.json,
     * se usará el globalMean para su encoding.
     *
     * Usando Set para búsquedas más eficientes O(1).
     */
    public static final Set<String> VALID_AIRPORT_CODES = Set.of(
            "ABE", "ABI", "ABQ", "ABY", "ACT", "ACV", "ACY", "AEX", "AGS", "ALB",
            "AMA", "ANC", "ASE", "ATL", "ATW", "AUS", "AVL", "AVP", "AZO", "BDL",
            "BFL", "BGM", "BGR", "BHM", "BIL", "BIS", "BKG", "BLI", "BMI", "BNA",
            "BOI", "BOS", "BQK", "BQN", "BRO", "BRW", "BTR", "BTV", "BUF", "BUR",
            "BWI", "BZN", "CAE", "CAK", "CDV", "CEC", "CHA", "CHO", "CHS", "CIC",
            "CID", "CLD", "CLE", "CLT", "CMH", "CMI", "CMX", "COD", "COS", "COU",
            "CPR", "CRP", "CRW", "CSG", "CVG", "CWA", "CYS", "DAB", "DAL", "DAY",
            "DCA", "DEN", "DFW", "DHN", "DLH", "DRO", "DSM", "DTW", "EAU", "ECP",
            "EGE", "EKO", "ELM", "ELP", "ERI", "EUG", "EVV", "EWR", "EYW", "FAI",
            "FAR", "FAT", "FAY", "FCA", "FLG", "FLL", "FNT", "FSD", "FSM", "FWA",
            "GCC", "GEG", "GFK", "GGG", "GJT", "GNV", "GPT", "GRB", "GRK", "GRR",
            "GSO", "GSP", "GTF", "GTR", "HDN", "HLN", "HNL", "HOU", "HPN", "HRL",
            "HSV", "HTS", "IAD", "IAH", "ICT", "IDA", "ILM", "IND", "ISP", "ITH",
            "ITO", "IYK", "JAC", "JAN", "JAX", "JFK", "JNU", "KOA", "KTN", "LAN",
            "LAS", "LAX", "LBB", "LCH", "LEX", "LFT", "LGA", "LGB", "LIH", "LIT",
            "LMT", "LNK", "LRD", "LSE", "LWS", "MAF", "MBS", "MCI", "MCO", "MDT",
            "MDW", "MEI", "MEM", "MFE", "MFR", "MGM", "MHK", "MHT", "MIA", "MKE",
            "MLB", "MLI", "MLU", "MOB", "MOD", "MOT", "MQT", "MRY", "MSN", "MSO",
            "MSP", "MSY", "MTJ", "MYR", "OAJ", "OAK", "OGG", "OKC", "OMA", "ONT",
            "ORD", "ORF", "OTH", "OTZ", "PAH", "PBI", "PDX", "PHF", "PHL", "PHX",
            "PIA", "PIH", "PIT", "PNS", "PSC", "PSE", "PSP", "PVD", "PWM", "RAP",
            "RDM", "RDU", "RIC", "RKS", "RNO", "ROA", "ROC", "ROW", "RST", "RSW",
            "SAF", "SAN", "SAT", "SAV", "SBA", "SBN", "SBP", "SCC", "SCE", "SDF",
            "SEA", "SFO", "SGF", "SGU", "SHV", "SIT", "SJC", "SJU", "SLC", "SMF",
            "SMX", "SNA", "SPI", "SRQ", "STL", "STT", "STX", "SUN", "SWF", "SYR",
            "TEX", "TLH", "TOL", "TPA", "TRI", "TUL", "TUS", "TVC", "TWF", "TXK",
            "TYS", "VLD", "VPS", "XNA", "YAK", "YUM", "ZZZ"
    );


    /**
     * Valor mínimo requerido para la distancia de vuelo en kilómetros.
     */
    public static final int MIN_DISTANCE_REQUIRED = 200;

    /**
     * Patrón Regex para códigos de aerolínea de 2 caracteres alfanuméricos.
     * Valida el formato antes de verificar contra la lista de códigos válidos.
     */
    public static final String AIRLINE_CODE_PATTERN = "^[A-Z0-9]{2}$";

    /**
     * Patrón Regex para códigos de aeropuerto de 3 letras mayúsculas.
     * Valida el formato antes de verificar contra la lista de códigos válidos.
     */
    public static final String AIRPORT_CODE_PATTERN = "^[A-Z]{3}$";
}
