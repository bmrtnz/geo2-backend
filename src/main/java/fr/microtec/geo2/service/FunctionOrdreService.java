package fr.microtec.geo2.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.FunctionResult;

@Service
public class FunctionOrdreService {

    public static FunctionResult parseResaAutoResult(FunctionResult res) {
        Map<String, Object> data = new HashMap<>();
        String[] result = (String[]) res.getData().get("result");
        List<HashMap<String, String>> out = new ArrayList<>();
        List.of(result).stream()
                .map(v -> v.split("¤"))
                .forEach(v -> {
                    HashMap<String, String> parsed = new HashMap<>();
                    parsed.put("orl_ref", v[0]);
                    parsed.put("orl_lig", v[1]);
                    parsed.put("resa_desc", v[2]);
                    parsed.put("info_stock", v[3]);
                    parsed.put("warning", v[4]);
                    parsed.put("statut", v[5]);
                    out.add(parsed);
                });
        data.put("result", out);
        res.setData(data);
        return res;
    }

}
