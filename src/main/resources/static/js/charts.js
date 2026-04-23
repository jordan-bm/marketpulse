// src/main/resources/static/js/charts.js

async function initCharts(ticker) {
    const gaugeEl = document.getElementById('sentimentGauge');
    const recEl = document.getElementById('recommendationBadge');

    try {
        const res = await fetch(`/api/chart/${ticker}`);
        if (!res.ok) throw new Error('No chart data');
        const data = await res.json();

        // ── Price Chart ──────────────────────────────────────────────────────
        const priceCtx = document.getElementById('priceChart').getContext('2d');
        new Chart(priceCtx, {
            type: 'line',
            data: {
                labels: data.priceLabels,
                datasets: [{
                    label: `${ticker} Price (USD)`,
                    data: data.prices,
                    borderColor: '#1B998B',
                    backgroundColor: 'rgba(27,153,139,0.08)',
                    borderWidth: 2.5,
                    pointRadius: 3,
                    pointBackgroundColor: '#1B998B',
                    tension: 0.3,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { labels: { color: '#CBD5E1', font: { size: 12 } } },
                    tooltip: { mode: 'index', intersect: false }
                },
                scales: {
                    x: {
                        ticks: { color: '#64748B', maxTicksLimit: 8 },
                        grid: { color: 'rgba(255,255,255,0.05)' }
                    },
                    y: {
                        ticks: { color: '#64748B', callback: v => '$' + v.toFixed(2) },
                        grid: { color: 'rgba(255,255,255,0.05)' }
                    }
                }
            }
        });

        // ── Sentiment Chart ───────────────────────────────────────────────────
        const sentCtx = document.getElementById('sentimentChart').getContext('2d');
        new Chart(sentCtx, {
            type: 'line',
            data: {
                labels: data.sentimentLabels,
                datasets: [{
                    label: 'Sentiment Score',
                    data: data.sentimentScores,
                    borderColor: '#F4C430',
                    backgroundColor: 'rgba(244,196,48,0.08)',
                    borderWidth: 2.5,
                    pointRadius: 3,
                    pointBackgroundColor: '#F4C430',
                    tension: 0.3,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { labels: { color: '#CBD5E1', font: { size: 12 } } },
                    tooltip: { mode: 'index', intersect: false }
                },
                scales: {
                    x: {
                        ticks: { color: '#64748B', maxTicksLimit: 8 },
                        grid: { color: 'rgba(255,255,255,0.05)' }
                    },
                    y: {
                        min: -1,
                        max: 1,
                        ticks: { color: '#64748B', stepSize: 0.5 },
                        grid: { color: 'rgba(255,255,255,0.05)' }
                    }
                }
            }
        });

        // ── Sentiment Gauge ───────────────────────────────────────────────────
        const score = data.averageSentiment;
        if (gaugeEl) {
            gaugeEl.className = 'sentiment-gauge';
            if (score > 0.2)       gaugeEl.classList.add('gauge-bullish');
            else if (score < -0.2) gaugeEl.classList.add('gauge-bearish');
            else                   gaugeEl.classList.add('gauge-neutral');
            gaugeEl.textContent = score > 0 ? `▲ ${score.toFixed(2)}` : `▼ ${Math.abs(score).toFixed(2)}`;
        }

        if (recEl) recEl.textContent = data.recommendation;

    } catch (err) {
        console.warn('Chart data not available:', err);
        if (gaugeEl) gaugeEl.textContent = '— No data';
        document.getElementById('chartsSection')?.style.setProperty('display', 'none');
    }
}