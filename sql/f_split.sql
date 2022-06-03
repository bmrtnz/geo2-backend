create or replace type p_str_tab_type is table of clob;

CREATE OR REPLACE PROCEDURE "F_SPLIT" (
    str in clob,
    delimiter in varchar2,
    arr out p_str_tab_type
)
AS
    pos number := 0;
    idx number := 1;
    work clob;
    word clob;
BEGIN
    arr := p_str_tab_type();
    work := trim(str);

    while work <> ''
        loop
            pos := instr(work, delimiter);

            if (pos = 0) then
                word := work;
                work := '';
            else
                word := substr(work, 1, pos - 1);
                work := substr(work, pos + 1, length(work));
            end if;

            arr.extend();
            arr(idx) := word;
            idx := idx + 1;
        end loop;
end;
/

