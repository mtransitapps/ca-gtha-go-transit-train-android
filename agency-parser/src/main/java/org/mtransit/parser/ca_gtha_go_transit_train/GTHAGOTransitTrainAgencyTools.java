package org.mtransit.parser.ca_gtha_go_transit_train;

import static org.mtransit.commons.Constants.SPACE_;
import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

// https://www.gotransit.com/en/information-resources/software-developers
// https://www.gotransit.com/fr/ressources-informatives/dveloppeurs-de-logiciel
public class GTHAGOTransitTrainAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new GTHAGOTransitTrainAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_EN_FR;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "GO Transit";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_TRAIN;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true;
	}

	@Override
	public @Nullable String getRouteIdCleanupRegex() {
		return "^\\d+-";
	}

	@Nullable
	@Override
	public Long convertRouteIdFromShortNameNotSupported(@NotNull String routeShortName) {
		switch (routeShortName) {
		case ST_RSN:
			return ST_RID;
		case RH_RSN:
			return RH_RID;
		case MI_RSN:
			return MI_RID;
		case LW_RSN:
			return LW_RID;
		case LE_RSN:
			return LE_RID;
		case KI_RSN:
		case GT_RSN:
			return KI_RID;
		case BR_RSN:
			return BR_RID;
		}
		return super.convertRouteIdFromShortNameNotSupported(routeShortName);
	}

	private static final long LW_RID = 1L; // Lakeshore West
	private static final long MI_RID = 2L; // Milton
	private static final long KI_RID = 3L; // Kitchener
	private static final long BR_RID = 5L; // Barrie
	private static final long RH_RID = 6L; // Richmond Hill
	private static final long ST_RID = 7L; // Stouffville
	private static final long LE_RID = 9L; // Lakeshore East

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(getFirstLanguageNN(), routeLongName);
	}

	private static final String ST_RSN = "ST"; // Stouffville
	private static final String RH_RSN = "RH"; // Richmond Hill
	private static final String MI_RSN = "MI"; // Milton
	private static final String LW_RSN = "LW"; // Lakeshore West
	private static final String LE_RSN = "LE"; // Lakeshore East
	private static final String KI_RSN = "KI"; // Kitchener
	private static final String GT_RSN = "GT"; // Kitchener (2)
	private static final String BR_RSN = "BR"; // Barrie

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR = "387C2B"; // GREEN (AGENCY WEB SITE CSS)

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Nullable
	@Override
	public String provideMissingRouteColor(@NotNull GRoute gRoute) {
		switch (getRouteShortName(gRoute)) {
		// @formatter:off
		case LW_RSN: return "96092B"; // Lakeshore West
		case MI_RSN: return "F46F1A"; // Milton
		case KI_RSN: return "098137"; // Kitchener
		case BR_RSN: return "0B335E"; // Barrie
		case RH_RSN: return "0098C9"; // Richmond Hill
		case ST_RSN: return "794500"; // Stouffville
		// TODO case 8: return "BC6277"; // Niagara Falls
		case LE_RSN: return "EE3124"; // Lakeshore East
		// @formatter:on
		}
		return super.provideMissingRouteColor(gRoute);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern START_WITH_RSN = Pattern.compile("(^[A-Z]{2}(\\s+)- )", Pattern.CASE_INSENSITIVE);

	private static final Pattern FIRST_STATION_TIME_LAST_STATION_TIME = Pattern.compile("(" //
			+ "([\\w\\s]*)" //
			+ "\\s+" //
			+ "(\\d{2}:\\d{2})" //
			+ "\\s+" //
			+ "-" //
			+ "\\s+" //
			+ "([\\w\\s]*)" //
			+ "\\s+" //
			+ "(\\d{2}:\\d{2})" //
			+ ")", Pattern.CASE_INSENSITIVE);
	private static final String FIRST_STATION_TIME_LAST_STATION_TIME_REPLACEMENT = "$4";

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = START_WITH_RSN.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = FIRST_STATION_TIME_LAST_STATION_TIME.matcher(tripHeadsign).replaceAll(FIRST_STATION_TIME_LAST_STATION_TIME_REPLACEMENT);
		tripHeadsign = GO.matcher(tripHeadsign).replaceAll(SPACE_);
		tripHeadsign = STATION.matcher(tripHeadsign).replaceAll(SPACE_);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		return CleanUtils.cleanLabel(getFirstLanguageNN(), tripHeadsign);
	}

	private static final Pattern GO = Pattern.compile("(^|\\W)(go)($|\\W)", Pattern.CASE_INSENSITIVE);

	private static final Pattern VIA = Pattern.compile("(^|\\s)(via)($|\\s)", Pattern.CASE_INSENSITIVE);

	private static final Pattern RAIL = Pattern.compile("(^|\\s)(rail)($|\\s)", Pattern.CASE_INSENSITIVE);

	private static final Pattern STATION = Pattern.compile("(^|\\s)(station)($|\\s)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = VIA.matcher(gStopName).replaceAll(SPACE_);
		gStopName = GO.matcher(gStopName).replaceAll(SPACE_);
		gStopName = RAIL.matcher(gStopName).replaceAll(SPACE_);
		gStopName = STATION.matcher(gStopName).replaceAll(SPACE_);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		return CleanUtils.cleanLabel(getFirstLanguageNN(), gStopName);
	}

	private static final String SID_UN = "UN";
	private static final int UN_SID = 9021;
	private static final String SID_EX = "EX";
	private static final int EX_SID = 9022;
	private static final String SID_MI = "MI";
	private static final int MI_SID = 9031;
	private static final String SID_LO = "LO";
	private static final int LO_SID = 9033;
	private static final String SID_DA = "DA";
	private static final int DA_SID = 9061;
	private static final String SID_SC = "SC";
	private static final int SC_SID = 9062;
	private static final String SID_EG = "EG";
	private static final int EG_SID = 9063;
	private static final String SID_GU = "GU";
	private static final int GU_SID = 9081;
	private static final String SID_RO = "RO";
	private static final int RO_SID = 9091;
	private static final String SID_PO = "PO";
	private static final int PO_SID = 9111;
	private static final String SID_CL = "CL";
	private static final int CL_SID = 9121;
	private static final String SID_OA = "OA";
	private static final int OA_SID = 9131;
	private static final String SID_BO = "BO";
	private static final int BO_SID = 9141;
	private static final String SID_AP = "AP";
	private static final int AP_SID = 9151;
	private static final String SID_BU = "BU";
	private static final int BU_SID = 9161;
	private static final String SID_AL = "AL";
	private static final int AL_SID = 9171;
	private static final String SID_PIN = "PIN";
	private static final int PIN_SID = 9911;
	private static final String SID_AJ = "AJ";
	private static final int AJ_SID = 9921;
	private static final String SID_WH = "WH";
	private static final int WH_SID = 9939;
	private static final String SID_OS = "OS";
	private static final int OS_SID = 9941;
	private static final String SID_BL = "BL";
	private static final int BL_SID = 9023;
	private static final String SID_KP = "KP";
	private static final int KP_SID = 9032;
	private static final String SID_WE = "WE";
	private static final int WE_SID = 9041;
	private static final String SID_ET = "ET";
	private static final int ET_SID = 9042;
	private static final String SID_OR = "OR";
	private static final int OR_SID = 9051;
	private static final String SID_OL = "OL";
	private static final int OL_SID = 9052;
	private static final String SID_AG = "AG";
	private static final int AG_SID = 9071;
	private static final String SID_DI = "DI";
	private static final int DI_SID = 9113;
	private static final String SID_CO = "CO";
	private static final int CO_SID = 9114;
	private static final String SID_ER = "ER";
	private static final int ER_SID = 9123;
	private static final String SID_HA = "HA";
	private static final int HA_SID = 9181;
	private static final String SID_YO = "YO";
	private static final int YO_SID = 9191;
	private static final String SID_SR = "SR";
	private static final int SR_SID = 9211;
	private static final String SID_ME = "ME";
	private static final int ME_SID = 9221;
	private static final String SID_LS = "LS";
	private static final int LS_SID = 9231;
	private static final String SID_ML = "ML";
	private static final int ML_SID = 9241;
	private static final String SID_KI = "KI";
	private static final int KI_SID = 9271;
	private static final String SID_MA = "MA";
	private static final int MA_SID = 9311;
	private static final String SID_BE = "BE";
	private static final int BE_SID = 9321;
	private static final String SID_BR = "BR";
	private static final int BR_SID = 9331;
	private static final String SID_MO = "MO";
	private static final int MO_SID = 9341;
	private static final String SID_GE = "GE";
	private static final int GE_SID = 9351;
	private static final String SID_GO = "GO";
	private static final int GO_SID = 2629;
	private static final String SID_AC = "AC";
	private static final int AC_SID = 9371;
	private static final String SID_GL = "GL";
	private static final int GL_SID = 9391;
	private static final String SID_EA = "EA";
	private static final int EA_SID = 9441;
	private static final String SID_LA = "LA";
	private static final int LA_SID = 9601;
	private static final String SID_RI = "RI";
	private static final int RI_SID = 9612;
	private static final String SID_MP = "MP";
	private static final int MP_SID = 9613;
	private static final String SID_RU = "RU";
	private static final int RU_SID = 9614;
	private static final String SID_KC = "KC";
	private static final int KC_SID = 9621;
	private static final String SID_AU = "AU";
	private static final int AU_SID = 9631;
	private static final String SID_NE = "NE";
	private static final int NE_SID = 9641;
	private static final String SID_BD = "BD";
	private static final int BD_SID = 9651;
	private static final String SID_BA = "BA";
	private static final int BA_SID = 9681;
	private static final String SID_AD = "AD";
	private static final int AD_SID = 9691;
	private static final String SID_MK = "MK";
	private static final int MK_SID = 9701;
	private static final String SID_UI = "UI";
	private static final int UI_SID = 9712;
	private static final String SID_MR = "MR";
	private static final int MR_SID = 9721;
	private static final String SID_CE = "CE";
	private static final int CE_SID = 9722;
	private static final String SID_MJ = "MJ";
	private static final int MJ_SID = 9731;
	private static final String SID_ST = "ST";
	private static final int ST_SID = 9741;
	private static final String SID_LI = "LI";
	private static final int LI_SID = 9742;
	private static final String SID_KE = "KE";
	private static final int KE_SID = 9771;
	private static final String SID_WR = "WR";
	private static final int WR_SID = 100001;
	private static final String SID_USBT = "USBT";
	private static final int USBT_SID = 100002;
	private static final String SID_NI = "NI";
	private static final int NI_SID = 100003;
	private static final String SID_PA = "PA";
	private static final int PA_SID = 100004;
	private static final String SID_SCTH = "SCTH";
	private static final int SCTH_SID = 100005;
	private static final String SID_DW = "DW";
	private static final int DW_SID = 100006;
	private static final String SID_BM = "BM";
	private static final int BM_SID = 100007;
	private static final String SID_LN = "LN";
	private static final int LN_SID = 100008;
	private static final String SID_SM = "SM";
	private static final int SM_SID = 100009;
	private static final String SID_SF = "SF";
	private static final int SF_SID = 100010;
	private static final String SID_CF = "CF";
	private static final int CF_SID = 100011;
	private static final String SID_MD = "MD";
	private static final int MD_SID = 100012;

	@Override
	public @Nullable Integer convertStopIdFromCodeNotSupported(@NotNull String stopCode) {
		final String stopId = stopCode.trim();
		switch (stopId) {
		case SID_UN:
			return UN_SID;
		case SID_EX:
			return EX_SID;
		case SID_MI:
			return MI_SID;
		case SID_LO:
			return LO_SID;
		case SID_DA:
			return DA_SID;
		case SID_SC:
			return SC_SID;
		case SID_EG:
			return EG_SID;
		case SID_GU:
			return GU_SID;
		case SID_RO:
			return RO_SID;
		case SID_PO:
			return PO_SID;
		case SID_CL:
			return CL_SID;
		case SID_OA:
			return OA_SID;
		case SID_BO:
			return BO_SID;
		case SID_AP:
			return AP_SID;
		case SID_BU:
			return BU_SID;
		case SID_AL:
			return AL_SID;
		case SID_PIN:
			return PIN_SID;
		case SID_AJ:
			return AJ_SID;
		case SID_WH:
			return WH_SID;
		case SID_OS:
			return OS_SID;
		case SID_BL:
			return BL_SID;
		case SID_KP:
			return KP_SID;
		case SID_WE:
			return WE_SID;
		case SID_ET:
			return ET_SID;
		case SID_OR:
			return OR_SID;
		case SID_OL:
			return OL_SID;
		case SID_AG:
			return AG_SID;
		case SID_DI:
			return DI_SID;
		case SID_CO:
			return CO_SID;
		case SID_ER:
			return ER_SID;
		case SID_HA:
			return HA_SID;
		case SID_YO:
			return YO_SID;
		case SID_SR:
			return SR_SID;
		case SID_ME:
			return ME_SID;
		case SID_LS:
			return LS_SID;
		case SID_ML:
			return ML_SID;
		case SID_KI:
			return KI_SID;
		case SID_MA:
			return MA_SID;
		case SID_BE:
			return BE_SID;
		case SID_BR:
			return BR_SID;
		case SID_MO:
			return MO_SID;
		case SID_GE:
			return GE_SID;
		case SID_GO:
			return GO_SID;
		case SID_AC:
			return AC_SID;
		case SID_GL:
			return GL_SID;
		case SID_EA:
			return EA_SID;
		case SID_LA:
			return LA_SID;
		case SID_RI:
			return RI_SID;
		case SID_MP:
			return MP_SID;
		case SID_RU:
			return RU_SID;
		case SID_KC:
			return KC_SID;
		case SID_AU:
			return AU_SID;
		case SID_NE:
			return NE_SID;
		case SID_BD:
			return BD_SID;
		case SID_BA:
			return BA_SID;
		case SID_BM:
			return BM_SID;
		case SID_AD:
			return AD_SID;
		case SID_MK:
			return MK_SID;
		case SID_UI:
			return UI_SID;
		case SID_MR:
			return MR_SID;
		case SID_CE:
			return CE_SID;
		case SID_MJ:
			return MJ_SID;
		case SID_ST:
			return ST_SID;
		case SID_LI:
			return LI_SID;
		case SID_KE:
			return KE_SID;
		case SID_WR:
			return WR_SID;
		case SID_USBT:
			return USBT_SID;
		case SID_NI:
			return NI_SID;
		case SID_PA:
			return PA_SID;
		case SID_SCTH:
			return SCTH_SID;
		case SID_DW:
			return DW_SID;
		case SID_LN:
			return LN_SID;
		case SID_SM:
			return SM_SID;
		case SID_SF:
			return SF_SID;
		case SID_CF:
			return CF_SID;
		case SID_MD:
			return MD_SID;
		default:
			return super.convertStopIdFromCodeNotSupported(stopCode);
		}
	}
}
