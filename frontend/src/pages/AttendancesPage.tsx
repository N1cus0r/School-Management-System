import LoadingSpinner from "../components/shared/LoadingSpinner.tsx";
import ErrorMessage from "../components/shared/ErrorMessage.tsx";
import ContentPlaceholder from "../components/shared/ContentPlaceholder.tsx";
import useAttendances from "../hooks/attendance/useAttendances.ts";
import { Grid, GridItem, Spinner } from "@chakra-ui/react";
import InfiniteScroll from "react-infinite-scroll-component";
import AttendancesTable from "../components/attendance/AttendancesTable.tsx";
import PageUtils from "../utils/page-utils.ts";
import AttendancesChart from "../components/attendance/AttendancesChart.tsx";

const AttendancesPage = () => {
  const {
    data: attendances,
    isLoading,
    error,
    fetchNextPage,
    hasNextPage,
  } = useAttendances();

  if (isLoading) return <LoadingSpinner />;

  if (!attendances || error) return <ErrorMessage />;

  if (attendances.pages[0].length === 0) return <ContentPlaceholder />;

  const fetchedDataCount = PageUtils.getFetchedDataCount(attendances);

  return (
    <Grid
      templateAreas={{
        base: `"main"`,
        lg: `"aside main"`,
      }}
      templateColumns={{
        base: "1fr",
        lg: "300px 1fr",
      }}
    >
      <GridItem area="aside">
        <AttendancesChart attendancePages={attendances.pages} />
      </GridItem>
      <GridItem area="main">
        <InfiniteScroll
          next={() => fetchNextPage()}
          hasMore={!!hasNextPage}
          loader={<Spinner />}
          dataLength={fetchedDataCount}
        >
          <AttendancesTable attendancePages={attendances.pages} />
        </InfiniteScroll>
      </GridItem>
    </Grid>
  );
};
export default AttendancesPage;
