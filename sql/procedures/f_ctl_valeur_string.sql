CREATE OR REPLACE PROCEDURE "F_CTL_VALEUR_STRING" (
    arg_nom_champ in varchar2,
    arg_len_val in number,
    arg_ctl in varchar2,
    ls_out out varchar2
)
AS
    ll_min number;
    ll_max number;
    ll_pos_tiret number;
    ll_pos_pv number;
    ll_ind number;
    ll_nb_tab number;
    ls_crlf varchar(10) := '~r~n';
    lb_retour integer := 0;
    a_traite clob;
    word clob;
    pos integer;
BEGIN
    If arg_ctl is null or arg_ctl = '' Then
        ls_out := '';
        return;
    end if;

    ll_pos_tiret := instr(arg_ctl, '-');
    If ll_pos_tiret > 0 Then
        ll_min := to_number(substr(arg_ctl, 1, ll_pos_tiret -1));
        ll_max := to_number(substr(arg_ctl, ll_pos_tiret - length(arg_ctl)));

        If arg_len_val < ll_min Then
            ls_out := '(A) %%% Le champ ''' || arg_nom_champ || ''' doit comporter au moins ' || to_char(ll_min) || ' chiffres' || ls_crlf;
            return;
        End IF;

        If arg_len_val > ll_max Then
            ls_out := '(A) %%% Le champ ''' || arg_nom_champ || ''' doit comporter  moins de ' || to_char(ll_max) || ' chiffres' || ls_crlf;
            return;
        End IF;
    Else
        a_traite := trim(arg_ctl);

        while a_traite is not null
            loop
                pos := instr(a_traite, ';');

                if (pos = 0) then
                    word := a_traite;
                    a_traite := '';
                else
                    word := substr(a_traite, 1, pos - 1);
                    a_traite := substr(a_traite, pos + 1, length(a_traite));
                end if;

                If arg_len_val = to_number(word) Then
                    ls_out := '';
                    return;
                Else
                    ls_out := ls_out || ' ' || word;
                end if;
            end loop;

        If lb_retour = 0 Then
            ls_out := '(A) %%% Le champ ''' || arg_nom_champ || ''' doit comporter ' || ls_out || '  chiffres exactement' || ls_crlf;
            return;
        End If;
    End If;
end;
/

