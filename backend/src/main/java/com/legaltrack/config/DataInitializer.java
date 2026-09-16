package com.legaltrack.config;

import com.legaltrack.entity.*;
import com.legaltrack.enums.*;
import com.legaltrack.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final CourtComplexRepository courtComplexRepository;
    private final CourtRepository courtRepository;
    private final PoliceStationRepository policeStationRepository;
    private final CaseTypeRepository caseTypeRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final LawyerProfileRepository lawyerProfileRepository;
    private final LawyerAvailabilityRepository lawyerAvailabilityRepository;
    private final LawyerSpecializationRepository lawyerSpecializationRepository;
    private final LawyerCourtRepository lawyerCourtRepository;
    private final LawyerLanguageRepository lawyerLanguageRepository;
    private final CaseFileRepository caseFileRepository;
    private final CasePartyRepository casePartyRepository;
    private final CaseAdvocateRepository caseAdvocateRepository;
    private final CaseEventRepository caseEventRepository;
    private final CaseDiaryEntryRepository caseDiaryEntryRepository;
    private final CaseHearingRepository caseHearingRepository;
    private final CaseOrderRepository caseOrderRepository;
    private final CaseAttentionRepository caseAttentionRepository;
    private final TrackedCaseRepository trackedCaseRepository;
    private final ClientLawyerRelationshipRepository relationshipRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final LegalAidEligibilityRuleRepository legalAidRuleRepository;
    private final LegalAidApplicationRepository legalAidAppRepository;
    private final NotificationRepository notificationRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final SupportMessageRepository supportMessageRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            log.info("Database already initialized with seed data.");
            return;
        }

        log.info("Seeding database with initial legal domain data, users, demo cases and rules...");

        // 1. Roles
        Role clientRole = roleRepository.save(Role.builder().name(RoleType.ROLE_CLIENT).build());
        Role lawyerRole = roleRepository.save(Role.builder().name(RoleType.ROLE_LAWYER).build());
        Role adminRole = roleRepository.save(Role.builder().name(RoleType.ROLE_ADMIN).build());
        Role officerRole = roleRepository.save(Role.builder().name(RoleType.ROLE_LEGAL_AID_OFFICER).build());
        Role supportRole = roleRepository.save(Role.builder().name(RoleType.ROLE_SUPPORT).build());

        // 2. Geographic Data
        State tn = stateRepository.save(State.builder().name("Tamil Nadu").code("TN").build());
        State ka = stateRepository.save(State.builder().name("Karnataka").code("KA").build());
        State kl = stateRepository.save(State.builder().name("Kerala").code("KL").build());

        District salem = districtRepository.save(District.builder().state(tn).name("Salem").code("SLM").build());
        District chennai = districtRepository.save(District.builder().state(tn).name("Chennai").code("CHN").build());
        District namakkal = districtRepository.save(District.builder().state(tn).name("Namakkal").code("NMK").build());
        District erode = districtRepository.save(District.builder().state(tn).name("Erode").code("ERD").build());
        District coimbatore = districtRepository.save(District.builder().state(tn).name("Coimbatore").code("CBE").build());
        District madurai = districtRepository.save(District.builder().state(tn).name("Madurai").code("MDU").build());

        CourtComplex salemComplex = courtComplexRepository.save(CourtComplex.builder()
                .district(salem)
                .name("Salem District Court Complex")
                .address("Court Road, Hastampatti, Salem, Tamil Nadu 636007")
                .build());

        CourtComplex chennaiComplex = courtComplexRepository.save(CourtComplex.builder()
                .district(chennai)
                .name("Madras High Court & City Civil Court Complex")
                .address("High Court Buildings, Parry's Corner, Chennai, Tamil Nadu 600104")
                .build());

        CourtComplex cbeComplex = courtComplexRepository.save(CourtComplex.builder()
                .district(coimbatore)
                .name("Coimbatore Combined Court Complex")
                .address("Arts College Road, Gopalapuram, Coimbatore, Tamil Nadu 641018")
                .build());

        Court pdjCourt = courtRepository.save(Court.builder()
                .courtComplex(salemComplex)
                .name("Principal District & Sessions Court, Salem")
                .courtType("DISTRICT_COURT")
                .judgeDesignation("Principal District Judge")
                .build());

        Court subCourt = courtRepository.save(Court.builder()
                .courtComplex(salemComplex)
                .name("Sub Court, Salem")
                .courtType("SUB_COURT")
                .judgeDesignation("Subordinate Judge")
                .build());

        Court ftcCourt = courtRepository.save(Court.builder()
                .courtComplex(salemComplex)
                .name("Additional District Court (FTC), Salem")
                .courtType("FAST_TRACK_COURT")
                .judgeDesignation("Additional District Judge")
                .build());

        Court chnCourt = courtRepository.save(Court.builder()
                .courtComplex(chennaiComplex)
                .name("City Civil Court (Court No. 1), Chennai")
                .courtType("CITY_CIVIL_COURT")
                .judgeDesignation("Principal Judge, City Civil Court")
                .build());

        Court cbeCourt = courtRepository.save(Court.builder()
                .courtComplex(cbeComplex)
                .name("Principal District Court, Coimbatore")
                .courtType("DISTRICT_COURT")
                .judgeDesignation("Principal District Judge")
                .build());

        PoliceStation ps1 = policeStationRepository.save(PoliceStation.builder()
                .district(salem)
                .name("Hasthampatti Police Station")
                .code("SLM-PS-01")
                .build());

        policeStationRepository.save(PoliceStation.builder().district(salem).name("Salem Town Police Station").code("SLM-PS-02").build());
        policeStationRepository.save(PoliceStation.builder().district(salem).name("Suramangalam Police Station").code("SLM-PS-03").build());
        policeStationRepository.save(PoliceStation.builder().district(chennai).name("Flower Bazaar Police Station").code("CHN-PS-01").build());
        policeStationRepository.save(PoliceStation.builder().district(coimbatore).name("Race Course Police Station").code("CBE-PS-01").build());

        // Case Types
        caseTypeRepository.save(CaseType.builder().name("Civil Suit (Original)").code("CS").category("CIVIL").description("Civil property & declaration suits").build());
        caseTypeRepository.save(CaseType.builder().name("Original Petition").code("OP").category("CIVIL").description("Succession, probate, arbitration").build());
        caseTypeRepository.save(CaseType.builder().name("Criminal Miscellaneous Petition").code("Crl.MP").category("CRIMINAL").description("Bail & anticipatory bail").build());
        caseTypeRepository.save(CaseType.builder().name("Calendar Case").code("CC").category("CRIMINAL").description("Trials on charge sheets").build());
        caseTypeRepository.save(CaseType.builder().name("Family Court Petition").code("HMOP").category("FAMILY").description("Matrimonial disputes & maintenance").build());
        caseTypeRepository.save(CaseType.builder().name("Motor Accident Claims").code("MCOP").category("MOTOR_ACCIDENT").description("Motor accident compensation claims").build());
        caseTypeRepository.save(CaseType.builder().name("Consumer Complaint").code("CC/DCDRC").category("CONSUMER").description("Consumer disputes").build());

        // 3. Users (Password for all: password123)
        String encodedPassword = passwordEncoder.encode("password123");

        User adminUser = userRepository.save(User.builder()
                .name("System Administrator")
                .email("admin@example.com")
                .password(encodedPassword)
                .mobile("9876543210")
                .status(UserStatus.ACTIVE)
                .roles(Set.of(adminRole))
                .build());

        User officerUser = userRepository.save(User.builder()
                .name("Legal Aid Officer - SLSA")
                .email("officer@example.com")
                .password(encodedPassword)
                .mobile("9876543211")
                .status(UserStatus.ACTIVE)
                .roles(Set.of(officerRole))
                .build());

        User lawyerUser1 = userRepository.save(User.builder()
                .name("Adv. Kumar S.")
                .email("lawyer1@example.com")
                .password(encodedPassword)
                .mobile("9876543212")
                .status(UserStatus.ACTIVE)
                .roles(Set.of(lawyerRole))
                .build());

        User lawyerUser2 = userRepository.save(User.builder()
                .name("Adv. Priya Lakshmi")
                .email("lawyer2@example.com")
                .password(encodedPassword)
                .mobile("9876543213")
                .status(UserStatus.ACTIVE)
                .roles(Set.of(lawyerRole))
                .build());

        User clientUser = userRepository.save(User.builder()
                .name("Ramesh Babu")
                .email("client@example.com")
                .password(encodedPassword)
                .mobile("9876543214")
                .status(UserStatus.ACTIVE)
                .roles(Set.of(clientRole))
                .build());

        User supportUser = userRepository.save(User.builder()
                .name("Customer Support Agent")
                .email("support@example.com")
                .password(encodedPassword)
                .mobile("9876543215")
                .status(UserStatus.ACTIVE)
                .roles(Set.of(supportRole))
                .build());

        // 4. Profiles
        clientProfileRepository.save(ClientProfile.builder()
                .user(clientUser)
                .address("45/12, Fairlands Main Road, Salem")
                .state(tn)
                .district(salem)
                .pincode("636016")
                .occupation("Small Business Owner")
                .build());

        LawyerProfile lp1 = lawyerProfileRepository.save(LawyerProfile.builder()
                .user(lawyerUser1)
                .barRegistrationNumber("MS/1420/2016")
                .enrollmentYear(2016)
                .experienceYears(10)
                .state(tn)
                .district(salem)
                .officeAddress("Suite 3B, Advocate Chambers, Hasthampatti, Salem")
                .bio("Practicing advocate specializing in civil property disputes, land acquisition, succession, and contractual litigation in Salem courts for over 10 years.")
                .verified(true)
                .build());

        lawyerAvailabilityRepository.save(LawyerAvailability.builder()
                .lawyer(lp1)
                .status(LawyerAvailabilityStatus.ACCEPTING_CLIENTS)
                .availableFrom(LocalDate.of(2026, 1, 1))
                .availableUntil(LocalDate.of(2026, 12, 31))
                .build());

        lawyerSpecializationRepository.save(LawyerSpecialization.builder().lawyer(lp1).specialization("Civil").build());
        lawyerSpecializationRepository.save(LawyerSpecialization.builder().lawyer(lp1).specialization("Property & Real Estate").build());
        lawyerSpecializationRepository.save(LawyerSpecialization.builder().lawyer(lp1).specialization("Land Acquisition").build());

        lawyerCourtRepository.save(LawyerCourt.builder().lawyer(lp1).court(pdjCourt).build());
        lawyerCourtRepository.save(LawyerCourt.builder().lawyer(lp1).court(subCourt).build());

        lawyerLanguageRepository.save(LawyerLanguage.builder().lawyer(lp1).language("Tamil").build());
        lawyerLanguageRepository.save(LawyerLanguage.builder().lawyer(lp1).language("English").build());

        LawyerProfile lp2 = lawyerProfileRepository.save(LawyerProfile.builder()
                .user(lawyerUser2)
                .barRegistrationNumber("MS/2890/2018")
                .enrollmentYear(2018)
                .experienceYears(8)
                .state(tn)
                .district(salem)
                .officeAddress("12A, Court Road, Opposite Sub Court, Salem")
                .bio("Dedicated advocate with extensive experience in family court matters, consumer protection, and property document verification.")
                .verified(true)
                .build());

        lawyerAvailabilityRepository.save(LawyerAvailability.builder()
                .lawyer(lp2)
                .status(LawyerAvailabilityStatus.ACCEPTING_CLIENTS)
                .availableFrom(LocalDate.of(2026, 1, 1))
                .availableUntil(LocalDate.of(2026, 12, 31))
                .build());

        lawyerSpecializationRepository.save(LawyerSpecialization.builder().lawyer(lp2).specialization("Family & Matrimonial").build());
        lawyerSpecializationRepository.save(LawyerSpecialization.builder().lawyer(lp2).specialization("Civil").build());
        lawyerSpecializationRepository.save(LawyerSpecialization.builder().lawyer(lp2).specialization("Consumer Protection").build());

        lawyerCourtRepository.save(LawyerCourt.builder().lawyer(lp2).court(pdjCourt).build());
        lawyerCourtRepository.save(LawyerCourt.builder().lawyer(lp2).court(subCourt).build());

        lawyerLanguageRepository.save(LawyerLanguage.builder().lawyer(lp2).language("Tamil").build());
        lawyerLanguageRepository.save(LawyerLanguage.builder().lawyer(lp2).language("English").build());
        lawyerLanguageRepository.save(LawyerLanguage.builder().lawyer(lp2).language("Telugu").build());

        // 5. Legal Aid Eligibility Rules
        legalAidRuleRepository.save(LegalAidEligibilityRule.builder().authority("Tamil Nadu SLSA").state(tn).category(LegalAidCategory.WOMAN_OR_CHILD).incomeLimit(new BigDecimal("300000.00")).effectiveFrom(LocalDate.of(2025, 1, 1)).active(true).build());
        legalAidRuleRepository.save(LegalAidEligibilityRule.builder().authority("Tamil Nadu SLSA").state(tn).category(LegalAidCategory.SCHEDULED_CASTE_OR_TRIBE).incomeLimit(new BigDecimal("300000.00")).effectiveFrom(LocalDate.of(2025, 1, 1)).active(true).build());
        legalAidRuleRepository.save(LegalAidEligibilityRule.builder().authority("Tamil Nadu SLSA").state(tn).category(LegalAidCategory.PERSON_WITH_DISABILITY).incomeLimit(new BigDecimal("300000.00")).effectiveFrom(LocalDate.of(2025, 1, 1)).active(true).build());
        legalAidRuleRepository.save(LegalAidEligibilityRule.builder().authority("Tamil Nadu SLSA").state(tn).category(LegalAidCategory.INDUSTRIAL_WORKMAN).incomeLimit(new BigDecimal("300000.00")).effectiveFrom(LocalDate.of(2025, 1, 1)).active(true).build());
        legalAidRuleRepository.save(LegalAidEligibilityRule.builder().authority("Tamil Nadu SLSA").state(tn).category(LegalAidCategory.PERSON_IN_CUSTODY).incomeLimit(new BigDecimal("300000.00")).effectiveFrom(LocalDate.of(2025, 1, 1)).active(true).build());
        legalAidRuleRepository.save(LegalAidEligibilityRule.builder().authority("Tamil Nadu SLSA").state(tn).category(LegalAidCategory.LOW_INCOME_GENERAL).incomeLimit(new BigDecimal("300000.00")).effectiveFrom(LocalDate.of(2025, 1, 1)).active(true).build());

        // 6. Demo Cases
        CaseFile case1 = caseFileRepository.save(CaseFile.builder()
                .internalReferenceId("REF-DEMO-2026-001")
                .client(clientUser)
                .lawyer(lawyerUser1)
                .court(pdjCourt)
                .state(tn)
                .district(salem)
                .courtComplex(salemComplex)
                .caseType("Civil Suit (Original)")
                .caseCategory("CIVIL")
                .title("DEMO PROPERTY DISPUTE (Ramesh Babu vs K. Sundaram & Ors)")
                .cnrNumber("DEMO123456")
                .caseNumber("CS/123/2026")
                .filingNumber("FIL/894/2026")
                .filingDate(LocalDate.of(2026, 3, 15))
                .registrationNumber("REG/123/2026")
                .registrationDate(LocalDate.of(2026, 4, 2))
                .act("Transfer of Property Act, 1882")
                .section("Section 54, Specific Relief Act Sec 34")
                .status(CaseStatus.PENDING)
                .stage(CaseStage.APPEARANCE)
                .matterDescription("Suit for declaration of title and permanent injunction regarding ancestral agricultural land in Salem Taluk.")
                .engagementType(EngagementType.PRIVATE_LAWYER)
                .isDemoData(true)
                .nextHearingDate(LocalDate.of(2026, 10, 14))
                .build());

        CaseFile case2 = caseFileRepository.save(CaseFile.builder()
                .internalReferenceId("REF-DEMO-2026-002")
                .client(clientUser)
                .lawyer(lawyerUser2)
                .court(pdjCourt)
                .state(tn)
                .district(salem)
                .courtComplex(salemComplex)
                .caseType("Original Petition")
                .caseCategory("CIVIL")
                .title("DEMO PARTITION MATTER (Ramesh Babu vs Legal Heirs of Murugesan)")
                .cnrNumber("DEMO234567")
                .caseNumber("OP/45/2026")
                .filingNumber("FIL/320/2026")
                .filingDate(LocalDate.of(2026, 5, 10))
                .registrationNumber("REG/45/2026")
                .registrationDate(LocalDate.of(2026, 5, 20))
                .act("Hindu Succession Act, 1956")
                .section("Section 6")
                .status(CaseStatus.LISTED)
                .stage(CaseStage.EVIDENCE)
                .matterDescription("Petition for partition of joint family residential property.")
                .engagementType(EngagementType.PRIVATE_LAWYER)
                .isDemoData(true)
                .nextHearingDate(LocalDate.of(2026, 10, 20))
                .build());

        // Parties
        casePartyRepository.save(CaseParty.builder().caseFile(case1).name("Ramesh Babu").partyType(PartyType.PLAINTIFF).isPrimary(true).contactInfo("9876543214").build());
        casePartyRepository.save(CaseParty.builder().caseFile(case1).name("K. Sundaram").partyType(PartyType.DEFENDANT).isPrimary(true).contactInfo("Opposite Party").build());
        casePartyRepository.save(CaseParty.builder().caseFile(case1).name("Village Administrative Officer, Salem").partyType(PartyType.RESPONDENT).isPrimary(false).contactInfo("Official").build());
        casePartyRepository.save(CaseParty.builder().caseFile(case2).name("Ramesh Babu").partyType(PartyType.PETITIONER).isPrimary(true).contactInfo("9876543214").build());
        casePartyRepository.save(CaseParty.builder().caseFile(case2).name("S. Murugesan & 2 Others").partyType(PartyType.RESPONDENT).isPrimary(true).contactInfo("Salem").build());

        // Advocates
        caseAdvocateRepository.save(CaseAdvocate.builder().caseFile(case1).advocateName("Adv. Kumar S.").registrationNumber("MS/1420/2016").partyRepresented("Plaintiff (Ramesh Babu)").role("Lead Counsel").build());
        caseAdvocateRepository.save(CaseAdvocate.builder().caseFile(case1).advocateName("Adv. V. Natarajan").registrationNumber("MS/889/2010").partyRepresented("Defendant (K. Sundaram)").role("Opposing Counsel").build());
        caseAdvocateRepository.save(CaseAdvocate.builder().caseFile(case2).advocateName("Adv. Priya Lakshmi").registrationNumber("MS/2890/2018").partyRepresented("Petitioner (Ramesh Babu)").role("Lead Counsel").build());

        // Timeline Events
        caseEventRepository.save(CaseEvent.builder().caseFile(case1).eventType("CASE_FILED").eventTitle("Case Filed").eventDescription("Civil Suit presented before Principal District Court registry.").eventDate(LocalDateTime.of(2026, 3, 15, 10, 30)).source(EventSource.MOCK_COURT_SYNC).createdBy(adminUser).build());
        caseEventRepository.save(CaseEvent.builder().caseFile(case1).eventType("CASE_REGISTERED").eventTitle("Case Registered & Numbered").eventDescription("Suit numbered as CS/123/2026.").eventDate(LocalDateTime.of(2026, 4, 2, 11, 0)).source(EventSource.MOCK_COURT_SYNC).createdBy(adminUser).build());
        caseEventRepository.save(CaseEvent.builder().caseFile(case1).eventType("FIRST_HEARING").eventTitle("First Hearing & Summons Issued").eventDescription("Summons ordered to defendant.").eventDate(LocalDateTime.of(2026, 5, 18, 10, 45)).source(EventSource.MOCK_COURT_SYNC).createdBy(adminUser).build());
        caseEventRepository.save(CaseEvent.builder().caseFile(case1).eventType("NOTICE_SERVED").eventTitle("Summons Served on Defendant").eventDescription("Service report submitted by Court Bailiff.").eventDate(LocalDateTime.of(2026, 7, 15, 11, 30)).source(EventSource.MOCK_COURT_SYNC).createdBy(adminUser).build());
        caseEventRepository.save(CaseEvent.builder().caseFile(case1).eventType("APPEARANCE").eventTitle("Appearance of Parties").eventDescription("Defendant appeared through counsel; posted for written statement.").eventDate(LocalDateTime.of(2026, 9, 30, 12, 0)).source(EventSource.MOCK_COURT_SYNC).createdBy(adminUser).build());

        // Chronological Case Diary Entries
        caseDiaryEntryRepository.save(CaseDiaryEntry.builder().caseFile(case1).createdBy(lawyerUser1).entryType(DiaryEntryType.DOCUMENT_REQUEST).title("Lawyer requested Sale Deed").description("Adv. Kumar requested certified parent sale deed of survey no 142/3.").eventDate(LocalDateTime.of(2026, 9, 25, 14, 0)).visibility(DiaryVisibility.SHARED).build());
        caseDiaryEntryRepository.save(CaseDiaryEntry.builder().caseFile(case1).createdBy(clientUser).entryType(DiaryEntryType.DOCUMENT_UPLOADED).title("Client uploaded Sale Deed").description("Ramesh Babu uploaded scanned PDF copy of 1998 registered sale deed.").eventDate(LocalDateTime.of(2026, 9, 27, 16, 30)).visibility(DiaryVisibility.SHARED).build());
        caseDiaryEntryRepository.save(CaseDiaryEntry.builder().caseFile(case1).createdBy(lawyerUser1).entryType(DiaryEntryType.HEARING).title("Hearing completed").description("Advocate argued for interim injunction extension; court directed filing of rejoinder.").eventDate(LocalDateTime.of(2026, 9, 30, 13, 0)).visibility(DiaryVisibility.SHARED).build());
        caseDiaryEntryRepository.save(CaseDiaryEntry.builder().caseFile(case1).createdBy(lawyerUser1).entryType(DiaryEntryType.HEARING).title("Next hearing scheduled: 14 Oct 2026").description("Matter adjourned to 14 Oct 2026 for framing of issues.").eventDate(LocalDateTime.of(2026, 9, 30, 13, 15)).visibility(DiaryVisibility.SHARED).build());
        caseDiaryEntryRepository.save(CaseDiaryEntry.builder().caseFile(case1).createdBy(lawyerUser1).entryType(DiaryEntryType.ORDER).title("Lawyer uploaded Order").description("Uploaded certified copy of Interim Injunction Extension Order.").eventDate(LocalDateTime.of(2026, 10, 1, 10, 0)).visibility(DiaryVisibility.SHARED).build());

        // Hearings
        caseHearingRepository.save(CaseHearing.builder().caseFile(case1).hearingDate(LocalDate.of(2026, 9, 30)).hearingTime("11:00 AM").court(pdjCourt).judgeName("Thiru. K. Rajasekaran, PDJ").purpose("Filing of Written Statement & Rejoinder").stage("Appearance").status(HearingStatus.COMPLETED).notes("Defendant filed written statement.").nextHearingDate(LocalDate.of(2026, 10, 14)).build());
        caseHearingRepository.save(CaseHearing.builder().caseFile(case1).hearingDate(LocalDate.of(2026, 10, 14)).hearingTime("10:30 AM").court(pdjCourt).judgeName("Thiru. K. Rajasekaran, PDJ").purpose("Framing of Issues & Rejoinder").stage("Issues").status(HearingStatus.SCHEDULED).notes("Counsel to submit proposed issues in property dispute.").build());
        caseHearingRepository.save(CaseHearing.builder().caseFile(case2).hearingDate(LocalDate.of(2026, 10, 20)).hearingTime("11:30 AM").court(pdjCourt).judgeName("Thiru. K. Rajasekaran, PDJ").purpose("Cross-examination of PW-1").stage("Evidence").status(HearingStatus.SCHEDULED).notes("Petitioner evidence continuation.").build());

        // Orders
        caseOrderRepository.save(CaseOrder.builder().caseFile(case1).orderDate(LocalDate.of(2026, 10, 1)).title("Interim Injunction Extension Order").orderType(OrderType.INTERIM_ORDER).summary("Status quo as on date of suit shall be maintained by both parties until next hearing date 14-10-2026.").build());
        caseOrderRepository.save(CaseOrder.builder().caseFile(case1).orderDate(LocalDate.of(2026, 5, 18)).title("Summons Issuance Order").orderType(OrderType.PROCEDURAL_ORDER).summary("Summons ordered to defendant on payment of process fee within 3 days.").build());

        // Case Attention Items
        caseAttentionRepository.save(CaseAttention.builder().caseFile(case1).type("HEARING_SOON").title("Hearing in 3 days").description("Upcoming hearing on 14 Oct 2026 requires preparation of issues.").severity(Severity.ACTION_REQUIRED).actionUrl("/client/cases/1/hearings").resolved(false).build());
        caseAttentionRepository.save(CaseAttention.builder().caseFile(case1).type("DOCUMENT_REQUIRED").title("Document requested by lawyer").description("Adv. Kumar requested survey sketch approved by Revenue Inspector.").severity(Severity.WARNING).actionUrl("/client/cases/1/documents").resolved(false).build());
        caseAttentionRepository.save(CaseAttention.builder().caseFile(case1).type("NEW_ORDER_AVAILABLE").title("New order available").description("Interim Injunction Extension Order dated 01 Oct 2026 is available for download.").severity(Severity.INFO).actionUrl("/client/cases/1/orders").resolved(false).build());
        caseAttentionRepository.save(CaseAttention.builder().caseFile(case1).type("SYNC_SUCCESS").title("Latest case information synchronized").description("Court data refreshed with mock court provider repository.").severity(Severity.INFO).actionUrl("/client/cases/1").resolved(true).resolvedAt(LocalDateTime.now()).build());

        // Tracked Cases
        trackedCaseRepository.save(TrackedCase.builder().user(clientUser).caseFile(case1).nickname("Ancestral Land Dispute - Salem").notificationsEnabled(true).trackedAt(LocalDateTime.now().minusDays(5)).build());
        trackedCaseRepository.save(TrackedCase.builder().user(clientUser).caseFile(case2).nickname("Family Partition Matter").notificationsEnabled(false).trackedAt(LocalDateTime.now().minusDays(3)).build());

        // Relationship & Conversation
        relationshipRepository.save(ClientLawyerRelationship.builder().client(clientUser).lawyer(lawyerUser1).caseFile(case1).status(RelationshipStatus.ACTIVE).build());
        Conversation conv = conversationRepository.save(Conversation.builder().client(clientUser).lawyer(lawyerUser1).caseFile(case1).build());

        messageRepository.save(Message.builder().conversation(conv).sender(lawyerUser1).messageType(MessageType.TEXT).content("Hello Mr. Ramesh, please upload the registered parent sale deed document for CS/123/2026 so we can prepare for the 14th Oct hearing.").sentAt(LocalDateTime.now().minusDays(2)).build());
        messageRepository.save(Message.builder().conversation(conv).sender(clientUser).messageType(MessageType.TEXT).content("I have uploaded the 1998 Sale Deed PDF to the case documents tab.").sentAt(LocalDateTime.now().minusDays(1)).build());
        messageRepository.save(Message.builder().conversation(conv).sender(lawyerUser1).messageType(MessageType.TEXT).content("Thank you. I reviewed the deed. We are well prepared for the framing of issues.").sentAt(LocalDateTime.now().minusHours(4)).build());

        // Legal Aid Application
        legalAidAppRepository.save(LegalAidApplication.builder()
                .applicationNumber("LA-TN-2026-0001")
                .client(clientUser)
                .court(pdjCourt)
                .fullName("Ramesh Babu")
                .dateOfBirth(LocalDate.of(1985, 6, 12))
                .gender("MALE")
                .phone("9876543214")
                .email("client@example.com")
                .address("45/12, Fairlands Main Road, Salem")
                .state(tn)
                .district(salem)
                .caseType("Civil Suit (Original)")
                .caseStage("Pre-litigation / Filing Stage")
                .matterDescription("Seeking legal aid assistance for defense against unlawful encroachment of residential plot.")
                .opponentInformation("Local builder claiming disputed right of way")
                .annualIncome(new BigDecimal("180000.00"))
                .employmentStatus("SELF_EMPLOYED")
                .selectedCategory(LegalAidCategory.LOW_INCOME_GENERAL)
                .supportingInformation("Annual family income certificate issued by Tahsildar submitted.")
                .status(LegalAidStatus.UNDER_REVIEW)
                .build());

        // Notifications
        notificationRepository.save(Notification.builder().user(clientUser).type(NotificationType.HEARING_REMINDER).title("Hearing in 3 Days").message("Your case CS/123/2026 has a scheduled hearing on 14 Oct 2026 at Principal District Court, Salem.").referenceType("CASE").referenceId(case1.getId()).actionUrl("/client/cases/" + case1.getId() + "/hearings").isRead(false).build());
        notificationRepository.save(Notification.builder().user(clientUser).type(NotificationType.ORDER_AVAILABLE).title("New Order Uploaded").message("Adv. Kumar uploaded Interim Injunction Extension Order for CS/123/2026.").referenceType("ORDER").referenceId(case1.getId()).actionUrl("/client/cases/" + case1.getId() + "/orders").isRead(false).build());
        notificationRepository.save(Notification.builder().user(lawyerUser1).type(NotificationType.LAWYER_REQUEST).title("New Client Request").message("Ramesh Babu requested representation for Civil property dispute.").referenceType("REQUEST").referenceId(1L).actionUrl("/lawyer/requests").isRead(true).build());

        // Support Ticket
        SupportTicket st = supportTicketRepository.save(SupportTicket.builder()
                .ticketNumber("TKT-2026-0042")
                .user(clientUser)
                .category(SupportCategory.CASE_TRACKING)
                .subject("Clarification regarding CNR tracking sync frequency")
                .description("How often does the mock court synchronization update hearing records for tracked cases?")
                .priority(SupportPriority.LOW)
                .status(SupportStatus.OPEN)
                .build());

        supportMessageRepository.save(SupportMessage.builder().ticket(st).sender(clientUser).message("Hi, I would like to know if CNR status updates occur daily or automatically upon hearing date changes.").build());

        log.info("LEGALTRACK seed data initialization completed successfully!");
    }
}
