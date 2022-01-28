import React, { useState } from "react";
import Dataset from "./components/Chart";
import Model from "./components/ModelChart";
import Benchmark from "./components/BenchmarkChart";
import InfiniteScroll from "react-infinite-scroll-component";
import { useEffect } from "react";

function App(props) {
  const componentObj = [
    { component: <Dataset /> },
    { component: <Model /> },
    { component: <Benchmark /> },
  ];
  const [data, setData] = useState([]);
  const [index, setIndex] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  const fetchNextComp = () => {
    if (index < componentObj.length) {
      console.log("inside if");
      setData((prev) => [...prev, componentObj[index].component]);
      setIndex((prev) => prev + 1);
    } else if (index === componentObj.length) {
      setHasMore(false);
    }
  };

  return (
    <InfiniteScroll
      dataLength={data.length}
      loader={<div>Loading charts....</div>}
      next={fetchNextComp}
      hasMore={hasMore}
    >
      {data}
    </InfiniteScroll>
  );
}
export default App;
