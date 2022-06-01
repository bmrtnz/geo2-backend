CREATE OR REPLACE PROCEDURE "F_SPLIT" (
    str in clob,
    delimiter in varchar2,
    arr out p_str_tab_type
)
AS
    type p_str_tab_type is table of clob INDEX BY pls_INTEGER;
    pos number := 0;
    idx number := 0;
    work clob;
    word clob;
BEGIN
    work := trim(str);

    while work is not null
    loop
        pos := instr(work, delimiter);

        if (pos = 0) then
            word := work;
            work := '';
        else
            word := substr(work, 1, pos - 1);
            work := substr(work, pos + 1, length(work));
        end if;

        arr(idx) := word;
        idx := idx + 1;
    end loop;
end;
/

