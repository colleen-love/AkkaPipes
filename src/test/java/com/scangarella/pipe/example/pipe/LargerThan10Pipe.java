package com.scangarella.pipe.example.pipe;

import com.scangarella.pipe.stereotype.FilterPipe;

public class LargerThan10Pipe extends FilterPipe<Integer> {
    @Override
    public Integer ingest(Integer integer) {
        return integer > 10 ? integer : null;
    }
}
