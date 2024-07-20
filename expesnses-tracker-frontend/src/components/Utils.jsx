

export function parseDateTimeStr(date) {
  if (!(date instanceof Date)) {
    throw new Error("Invalid date object");
  }

  // const jodaDateTime = LocalDateTime.of(
  //     date.getFullYear(),
  //     date.getMonth() + 1, // Month is zero-based in JavaScript
  //     date.getDate(),
  //     date.getHours(),
  //     date.getMinutes(),
  //     date.getSeconds()
  // );

  // const formatter = DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss');
  // const formattedDateTime = jodaDateTime.format(formatter);

  // return formattedDateTime;
  // const formattedDate = date.toLocaleString('en-US', {
  //     year: 'numeric',
  //     month: '2-digit',
  //     day: '2-digit',
  //     hour: '2-digit',
  //     minute: '2-digit',
  //     second: '2-digit',
  //     hour12: false // Use 24-hour format
  // });

  // return formattedDate;
  // return (
  //   date.getFullYear() +
  //   "-" +
  //   ("0" + (date.getMonth() + 1)).slice(-2) +
  //   "-" +
  //   ("0" + date.getDate()).slice(-2) +
  //   " " +
  //   ("0" + date.getHours()).slice(-2) +
  //   ":" +
  //   ("0" + date.getMinutes()).slice(-2) +
  //   ":" +
  //   ("0" + date.getSeconds()).slice(-2)
  // );
  // return date.toISOString().replace("T"," ").substring(0, 19);
  return date.toISOString().substring(0, 19);

}
