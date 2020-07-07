-------------------
-- geo_client_cert
-------------------

create table geo_client_cert (
    cli_ref varchar(6) constraint fk_certif_client references GEO_CLIENT(CLI_REF),
    k_certif number constraint fk_certif_certif references GEO_CERTIF(K_CERTIF),
    constraint pk_clientcertif primary key (cli_ref, k_certif)
);

create or replace trigger GEO_CLI_CERT_BEF_INS_UPD_DEL
    before insert or update or delete
    on GEO_CLIENT_CERT
    for each row
declare
    CLIREF GEO_CLIENT.CLI_REF%TYPE;
    l_text VARCHAR2(100);
begin
    CLIREF := COALESCE(:NEW.cli_ref, :OLD.cli_ref);

    FOR cur_rec IN (SELECT k_certif FROM GEO_CLIENT_CERT WHERE CLI_REF = CLIREF)
        LOOP
            l_text := l_text || ',' || cur_rec.k_certif;
        END LOOP;
    UPDATE GEO_CLIENT SET CERTIFS = LTRIM(l_text, ',') WHERE CLI_REF = CLIREF;
end;
/

create or replace PROCEDURE INIT_CLIENT_CERTIF
    IS
    cursor c_clients
        is
        select CLI_REF, CERTIFS
        from GEO_CLIENT
        where certifs is not null and trim(certifs) is not null;
    a_traite varchar2(100);
    pos_virgule integer;
    certif_en_cours varchar2(20);
BEGIN
    DELETE FROM GEO_CLIENT_CERT;
    for r_client in c_clients loop
            begin
                a_traite := r_client.CERTIFS;
                while (a_traite is not null) LOOP
                        begin
                            pos_virgule := instr(a_traite, ',');

                            if (pos_virgule = 0) THEN
                                certif_en_cours := trim(a_traite);
                                a_traite := '';
                            else
                                certif_en_cours := trim(substr(a_traite, 1, pos_virgule - 1));
                                a_traite := trim(substr(a_traite, pos_virgule + 1, length(a_traite)));
                            end if;

                            insert into GEO_CLIENT_CERT(cli_ref, k_certif)
                            values (r_client.CLI_REF, CAST(certif_en_cours as NUMBER));
                        END;
                    end loop;
            end;
        end loop;
END INIT_CLIENT_CERTIF;

-------------------
-- geo_fourni_cert
-------------------
create table geo_fourni_cert(
    fou_code varchar(12) constraint fk_fournicertif_fourni references GEO_FOURNI(FOU_CODE),
    k_certif number constraint fk_fournicertif_certif references GEO_CERTIF(K_CERTIF),
    constraint pk_fournisseurcertif primary key (fou_code, k_certif)
);

-- En attente d'un retour de leur part pour savoir comment on gère ca coté fournisseur
/*create trigger GEO_FOUR_CERT_AFT_INS_UPD_DEL
    after insert or update or delete
    on GEO_FOURNI_CERT
    for each row
declare
    id GEO_FOURNI.FOU_CODE%TYPE;
    certif GEO_CERTIF.K_CERTIF%TYPE;
    value varchar(1);
begin
    if DELETING then
        id = :old.fou_code;
        certif = :old.k_certif;
        value = 'N';
    else
        id = :new.fou_code;
        certif = :new.k_certif;
        value = 'O';
    end if;

    if(:new.k_certif = 6) then
        update GEO_FOURNI set EUREPGAP = :value where fou_code = :id;
    else if (:new.k_certif = 7) then
        -- Quel champ ?
        update GEO_FOURNI set IND_CERT_VER = :value where fou_code = :id;
    else if (:new.k_vertif = 8) then
        -- Quel champ ?
        update GEO_FOURNI set IND_CERT_VER = :value where fou_code = :id;
    else if (:new.k_certif = 9) then
        update GEO_FOURNI set IND_CERT_VER = :value where fou_code = :id;
    end if;
end;*/

 /*
CREATE OR REPLACE PROCEDURE INIT_GEO2
IS
BEGIN

    PUT_LINE('PROCEDURE INIT_CLIENT_CERTIF');
    ALTER TRIGGER GEO_CLI_CERT_BEF_INS_UPD_DEL DISABLE;
    INIT_CLIENT_CERTIF();
    ALTER TRIGGER GEO_CLI_CERT_BEF_INS_UPD_DEL ENABLE;
    PUT_LINE(' ');

    -- FOUNISSEUR CERTIF

    PUT_LINE('PROCEDURE AVI_MIGRATION_ARTICLE');
    AVI_MIGRATION_ARTICLE();
    PUT_LINE(' ');


END INIT_GEO2;
*/
